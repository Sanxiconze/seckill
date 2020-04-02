package com.seckill.web;

import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExcution;
import com.seckill.dto.SeckillResult;
import com.seckill.entity.Seckill;
import com.seckill.enums.SeckillStatEnum;
import com.seckill.exception.RepeatKillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.exception.SeckillException;
import com.seckill.service.SeckillSerivce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by wangtc on 2020/3/30
 **/

@Controller
@RequestMapping("/seckill")//url:模块/资源/{id}/细分  /seckill/list
public class SeckillController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillSerivce seckillSerivce;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list( Model model ) {
        //获取列表页
        List<Seckill> list = seckillSerivce.getSeckillList();
        model.addAttribute("list", list);
        //list.jsp + model = ModelAndView
        return "list";
    }

    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail( @PathVariable("seckillId") Long seckillId, Model model ) {
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillSerivce.getById(seckillId);
        if (seckill == null) {
            return "redirect:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"}
    )
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId ) {
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillSerivce.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/{seckillId}/{md5}/exection",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"}
    )
    @ResponseBody
    public SeckillResult<SeckillExcution> excute(
            @PathVariable("seckillId") Long seckillId,
            @PathVariable("md5") String md5,
            @CookieValue(value = "killPhone", required = false) Long phone ) {
        SeckillResult<SeckillExcution> result;
        if (phone == null) {
            result = new SeckillResult<SeckillExcution>(false, "phone为空");
        } else {
            try {
                /**
                 * 采用存储过程优化，避免在事务中出现网络延迟，
                 */
                //SeckillExcution excution = seckillSerivce.executeSeckill(seckillId, phone, md5);
                SeckillExcution excution = seckillSerivce.executeSeckillByProcedure(seckillId, phone, md5);
                result = new SeckillResult<SeckillExcution>(true, excution);
            } catch (RepeatKillException e) {
                logger.error(e.getMessage(), e);
                SeckillExcution excution = new SeckillExcution(seckillId, SeckillStatEnum.REPEAT_KILL);
                result = new SeckillResult<SeckillExcution>(true, excution);
            } catch (SeckillCloseException e) {
                logger.error(e.getMessage(), e);
                SeckillExcution excution = new SeckillExcution(seckillId, SeckillStatEnum.END);
                result = new SeckillResult<SeckillExcution>(true, excution);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                SeckillExcution excution = new SeckillExcution(seckillId, SeckillStatEnum.INNER_ERROR);
                result = new SeckillResult<SeckillExcution>(true, excution);
            }
        }
        return result;
    }

    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time(){
        Date nowTime = new Date();
        return new SeckillResult(true,nowTime);
    }


}
