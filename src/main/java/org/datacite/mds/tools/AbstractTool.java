package org.datacite.mds.tools;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class AbstractTool {
    
    private static final String APPLICATION_CONTEXT = "META-INF/spring/applicationContext.xml";
    
    private static final ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT);
    
    @SuppressWarnings("unchecked")
    public static final void initAndRun(String[] args) {
        try {
            String callingClassName = Thread.currentThread().getStackTrace()[2].getClassName();
            Class<AbstractTool> callingClass = (Class<AbstractTool>) Class.forName(callingClassName);
            AbstractTool tool = context.getBean(callingClass);
            tool.run(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public abstract void run(String[] args);
}
