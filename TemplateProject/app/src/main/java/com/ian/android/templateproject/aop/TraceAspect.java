package com.ian.android.templateproject.aop;

import com.ian.android.templateproject.utils.LogUtil;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * @author Ian
 * @date 2016-01-25 10:16
 * <p/>
 * AOP 用户数据采集打点信息管理
 * 切面（统一处理）类
 * - 1、记录工程里需要打点的方法
 * after,before,around,throwing,returning Advice.
 */
@Aspect
public class TraceAspect {

    private static final String TAG = "TraceAspect";

    //切入点规则  匹配controller和base包下所有的方法
    private static final String POINTCUT_CONTROLLER =
            "execution(* com.ian.android.templateproject.controller..*.* (..))";

    private static final String POINTCUT_BASE =
            "execution(* com.ian.android.templateproject.base..*.* (..))";

    /**
     * Pointcut
     * 定义Pointcut，Pointcut的名称为pointcutController()，此方法没有返回值和参数
     * 该方法就是一个标识，不进行调用
     */
    @Pointcut(POINTCUT_CONTROLLER)
    public void pointcutController() {
    }

    @Pointcut(POINTCUT_BASE)
    public void pointcutBase() {
    }

    /**
     * Before
     * 在核心业务执行前执行，不能阻止核心业务的调用。
     * @param joinPoint
     */
    @Before("pointcutBase()")
    public void beforeAdvice(JoinPoint joinPoint) {
//        LogUtil.d("-----beforeAdvice().invoke-----");
//
//        Object object = joinPoint.getTarget();
//        //获取类名&方法名
//        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//        String className = methodSignature.getDeclaringType().getName();
//        String methodName = methodSignature.getName();
//
//        LogUtil.d("className="+className+" methodName="+methodName);
//
//        LogUtil.d(" 此处意在执行核心业务逻辑前，做一些安全性的判断等等");
//        LogUtil.d(" 可通过joinPoint来获取所需要的内容");
//        LogUtil.d("-----End of beforeAdvice()------");
    }

    /**
     * After
     * 核心业务逻辑退出后（包括正常执行结束和异常退出），执行此Advice
     * @param joinPoint
     */
//    @After("pointcutBase()")
//    public void afterAdvice(JoinPoint joinPoint) {
//        LogUtil.d("-----afterAdvice().invoke-----");
//        LogUtil.d(" 此处意在执行核心业务逻辑之后，做一些日志记录操作等等");
//        LogUtil.d(" 可通过joinPoint来获取所需要的内容");
//        LogUtil.d("-----End of afterAdvice()------");
//    }

    /**
     * Around
     * 手动控制调用核心业务逻辑，以及调用前和调用后的处理,
     *
     * 注意：当核心业务抛异常后，立即退出，转向AfterAdvice
     * 执行完AfterAdvice，再转到ThrowingAdvice
     * @param pjp
     * @return
     * @throws Throwable
     */
    //解析匹配 并记录点信息
    @Around("pointcutController()")
    public Object recordMediator(ProceedingJoinPoint joinPoint) throws Throwable {
        Object object = joinPoint.proceed();
        //获取类名&方法名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getName();
        String methodName = methodSignature.getName();

        // 判断是否为需要监听的方法
        if (GAConfig.GACMap.containsKey(className + GAConfig.TAG + methodName)) {
            //从定制中找到编码和描叙
            String code = GAConfig.GACMap.get(className + GAConfig.TAG + methodName);
            String log = className + " - " + methodName + " & " + code;

            if (code != null) {
                // 页面打点
                if (code.indexOf(GAConfig.TAG) > 0) {
                    String GACode = code.split(GAConfig.TAG)[0];
                    if(GAConfig.PAGE.equals(code.split(GAConfig.TAG)[1])){
                        // TODO 处理页面打点
                    }else{
                        // TODO 处理页面操作打点
                    }
                    LogUtil.e(TAG, log + " = " + getDescByCode(GACode));
                } else {
                    // TODO 没有设置的认为是: 页面操打点
                    LogUtil.e(TAG, log + " = " + getDescByCode(code));
                }
            } else {
                LogUtil.e(TAG, className + "的" + methodName + "方法出现解析异常 - 请确认GAConfig的配置是否符合规则 ");
            }
        }
        return object;
    }

    /**
     * AfterReturning
     * 核心业务逻辑调用正常退出后，不管是否有返回值，正常退出后，均执行此Advice
     * @param joinPoint
     */
//    @AfterReturning(value = "pointcutController()", returning = "retVal")
//    public void afterReturningAdvice(JoinPoint joinPoint, String retVal) {
//        LogUtil.d("-----afterReturningAdvice().invoke-----");
//        LogUtil.d("Return Value: " + retVal);
//        LogUtil.d(" 此处可以对返回值做进一步处理");
//        LogUtil.d(" 可通过joinPoint来获取所需要的内容");
//        LogUtil.d("-----End of afterReturningAdvice()------");
//    }

    /**
     * 核心业务逻辑调用异常退出后，执行此Advice，处理错误信息
     *
     * 注意：执行顺序在Around Advice之后
     * @param joinPoint
     * @param ex
     */
//    @AfterThrowing(value = "pointcutController()", throwing = "ex")
//    public void afterThrowingAdvice(JoinPoint joinPoint, Exception ex) {
//        LogUtil.d("-----afterThrowingAdvice().invoke-----");
//        LogUtil.d(" 错误信息："+ex.getMessage());
//        LogUtil.d(" 此处意在执行核心业务逻辑出错时，捕获异常，并可做一些日志记录操作等等");
//        LogUtil.d(" 可通过joinPoint来获取所需要的内容");
//        LogUtil.d("-----End of afterThrowingAdvice()------");
//    }

    private String getDescByCode(String code) {
        return GAConfig.GAStrMap.get(code);
    }

}
