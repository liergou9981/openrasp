/*
 * Copyright 2017-2018 Baidu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baidu.openrasp.hook.server.jetty;

import com.baidu.openrasp.HookHandler;
import com.baidu.openrasp.cloud.model.ErrorType;
import com.baidu.openrasp.cloud.utils.CloudUtils;
import com.baidu.openrasp.hook.server.ServerXssHook;
import com.baidu.openrasp.plugin.checker.CheckParameter;
import com.baidu.openrasp.tool.Reflection;
import com.baidu.openrasp.tool.annotation.HookAnnotation;
import com.baidu.openrasp.tool.hook.ServerXss;
import com.baidu.openrasp.tool.model.ApplicationModel;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author anyang
 * @Description: jetty xss检测的hook点
 * @date 2018/8/1315:13
 */
@HookAnnotation
public class JettyXssHook extends ServerXssHook {

    @Override
    public boolean isClassMatched(String className) {
        return "org/eclipse/jetty/server/AbstractHttpConnection".equals(className);
    }

    @Override
    protected void hookMethod(CtClass ctClass) throws IOException, CannotCompileException, NotFoundException {

        String src = getInvokeStaticSrc(JettyXssHook.class, "getJettyOutputBuffer", "_generator", Object.class);
        insertBefore(ctClass, "completeResponse", "()V", src);

    }


    public static void getJettyOutputBuffer(Object object) {
        try {
            Object buffer = Reflection.getSuperField(object, "_buffer");
            String content = new String(buffer.toString().getBytes(), "utf-8");
            if (HookHandler.requestCache.get() != null) {
                HashMap<String, Object> params = ServerXss.generateXssParameters(content);
                HookHandler.doCheck(CheckParameter.Type.XSS, params);
            }
        } catch (Exception e) {
            String message = ApplicationModel.getServerName() + " xss detectde failed";
            int errorCode = ErrorType.HOOK_ERROR.getCode();
            HookHandler.LOGGER.warn(CloudUtils.getExceptionObject(message, errorCode), e);
        }
    }
}
