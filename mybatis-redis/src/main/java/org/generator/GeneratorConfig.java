package org.generator;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GeneratorConfig {
    public static void main(String[] args) throws Exception{
        List<String> warnings= new ArrayList<>();
        //覆盖原代码
        boolean overwrite= true;
        InputStream is= GeneratorConfig.class.getResourceAsStream("/generator/generatorConfig.xml");
        ConfigurationParser cp= new ConfigurationParser(warnings);
        Configuration config= cp.parseConfiguration(is);
        is.close();

        DefaultShellCallback callback= new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator= new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
        for(String warning: warnings){
            System.out.println(warning);
        }
    }
}
