package com.example.seckill2.validator;


import com.example.seckill2.util.PhoneValidatorUtil;
import org.apache.commons.lang3.StringUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 验证手机号格式
 * @author 28614
 * @date 2019/12/17 20:17
 */
public class MobileFormatValidator implements ConstraintValidator<IsMobile,String> {

    private boolean required = false;


    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    /**
     *
     * @param s : 手机号
     * @param constraintValidatorContext : IsMobile注解
     * @return
     */
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(required){//一定要填写
            return PhoneValidatorUtil.isMobile(s);
        }
        else{//不一定需要填写
            if(StringUtils.isEmpty((s))){
                return true;
            }
            else{
                return PhoneValidatorUtil.isMobile(s);
            }
        }
    }
}
