package com.idea.demo;

import com.alibaba.fastjson.JSONObject;
import com.idea.demo.bean.Person;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;

import java.util.LinkedList;
import java.util.List;

public class DroolsTest {

    public static final String SERVER_URL = "https://kie-server-showcase.amoylabs.com:8443/kie-server/services/rest/server";
    public static final String PASSWORD = "admin";
    public static final String USERNAME = "admin";
    public static final String KIE_CONTAINER_ID = "demo_1.0.0";

    public static void main(String[] args) {
        // KisService 配置信息设置
        KieServicesConfiguration kieServicesConfiguration =
                KieServicesFactory.newRestConfiguration(SERVER_URL, USERNAME, PASSWORD, 10000L);
        kieServicesConfiguration.setMarshallingFormat(MarshallingFormat.JSON);

        // 创建规则服务客户端
        KieServicesClient kieServicesClient = KieServicesFactory.newKieServicesClient(kieServicesConfiguration);
        RuleServicesClient ruleServicesClient = kieServicesClient.getServicesClient(RuleServicesClient.class);

        // 规则输入条件
        Person person = new Person();
        person.setName("NG");
        person.setAge(30);
        person.setSexy("boy");

        // 命令定义，包含插入数据，执行规则
        KieCommands kieCommands = KieServices.Factory.get().getCommands();
        List<Command<?>> commands = new LinkedList<Command<?>>();
        commands.add(kieCommands.newInsert(person, "Person"));
        commands.add(kieCommands.newFireAllRules());
        ServiceResponse<ExecutionResults> results = ruleServicesClient.executeCommandsWithResults(KIE_CONTAINER_ID,
                kieCommands.newBatchExecution(commands,"session1"));

        // 返回值读取
        Person value = (Person) results.getResult().getValue("Person");
        System.out.println(JSONObject.toJSON(value).toString());
    }

}
