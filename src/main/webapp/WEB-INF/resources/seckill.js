//存放主要交互逻辑js代码
//js模块化
var seckill = {

    //封装秒杀相关ajax的url
    URL: {
        now : function () {
            return '/seckill/time/now';
        },
        exposer:function(seckillId){
            return '/seckill/' + seckillId + '/exposer';
        },
        excution:function(seckillId,md5){
            return  '/seckill/'+seckillId + '/' + md5 + '/exection';
        }


    },
    //秒杀页详情逻辑
    validatePhone: function (phone) {
        if (phone && phone.length == 11 && !isNaN(phone))
            return true;
        else
            return false;
    },
    handleSeckillKill :function(seckillId,node){
        //获取秒杀地址，控制显示逻辑，执行秒杀
        node.hide()
            .html('<button class = "btn btn-primary btn-lg" id = "killBtn">开始秒杀</button>')

        $.post(seckill.URL.exposer(seckillId),{},function(result){
            //回调函数中，执行交互流程
            if(result && result['success']){
                var exposer = result['data'];
                console.log(exposer);
                if(exposer['exposed']) {
                    //开启秒杀
                    //获取秒杀地址。
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.excution(seckillId,md5);
                    console.log("killUrl :" + killUrl);
                    $('#killBtn').one('click',function(){
                        //执行一次，
                        //1.禁用
                        $(this).addClass('disabled');
                        //2.秒杀
                        $.post(killUrl,{},function(result){
                            if(result && result['success']){
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                //显示秒杀结果
                                node.html('<span class="label label-'+
                                    (state==1?'success':'danger')+'">' + stateInfo + '</span>');
                            }
                        });
                    });
                    //配置好按钮，绑定完时间，在将按钮显示
                    node.show();

                }else{
                    //未开启秒杀
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];

                    //重新计算时间
                    seckill.count_down(seckillId,now,start,end);
                }
            }else{
                console.log('result:'+result);
            }

        });

    },
    count_down :function(seckillId,nowTime,startTime,endTime){
        var seckillBox = $("#seckill-box")
        //时间判断
        if(nowTime > endTime){
            //秒杀结束
            seckillBox.html("秒杀结束!");
        }else if(nowTime < startTime){
            //秒杀未开始，计时时间绑定
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime,function(event){
                var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒')
                seckillBox.html(format);
            }).on('finish.countdown',function(){//时间完成回调函数
                //获取秒杀地址，执行秒杀
                seckill.handleSeckillKill(seckillId,seckillBox);
            });
        }else{
            //秒杀开始
            seckill.handleSeckillKill(seckillId,seckillBox);
        }
    },
    detail: {
        //详情页初始化
        init: function (params) {
            //手机验证，登录，计时交互
            //规划交互流程
            //在cookie中查找手机号
            var killPhone = $.cookie('killPhone');
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            //验证手机号
            if (!seckill.validatePhone(killPhone)) {
                //绑定phone
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true,//显示弹出层
                    backdrop: 'static',//禁止位置关闭
                    keyboard: false //关闭键盘事件
                })

                //控制输出
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    if (seckill.validatePhone(inputPhone)) {
                        //电话写入cookie
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'})
                        //刷新页面
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide()
                            .html('<label class="label label-danger">手机号错误</label>')
                            .show(300);
                    }
                })
            }

            $.get(seckill.URL.now(), {}, function (result) {
                if(result && result['success']){
                    var nowTime = result['data'];
                    //时间判断
                    seckill.count_down(seckillId,nowTime,startTime,endTime)
                }else{
                    console.log('result:' + result);
                }
            });
        }
    }
}