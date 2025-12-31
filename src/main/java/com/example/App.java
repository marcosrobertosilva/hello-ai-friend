package com.example;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner userinput;      // user inputted line as a Scanner
        String cmdline;

        ChatModel cmodel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("mr_robot_v2")
                .timeout(java.time.Duration.ofMinutes(2))
                .build();

        ChatMemory cm = MessageWindowChatMemory.withMaxMessages(20);

        // Load context once - this won't be in chat memory, but sent with first system message
        String context = readResourceFile("mr_robot_all_episodes_summary.txt");
        
        SystemMessage sysmsg = new SystemMessage("""
                    You are a specialist in the Mr Robot TV series and capable of answering questions about this show.
                    
                    Here is context about all episodes:
                    """ + context);
        cm.add(sysmsg);

        while (true) {
            System.out.print("prompt> ");

            userinput = new Scanner(System.in);
            cmdline = userinput.nextLine();

            if (cmdline.isBlank())       // If nothing, do nothing
                continue;

            UserMessage usrmsg = UserMessage.from(cmdline);
            cm.add(usrmsg);

            var answer = cmodel.chat(cm.messages());  // send the context as messages and save the response
            var response = answer.aiMessage().text();

            System.out.println(response);

            cm.add(new AiMessage(response));     // Add the AI response to memory

        }
    }

    private static String readResourceFile(String fileName) {
        try (InputStream is = App.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new RuntimeException("Resource file not found: " + fileName);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource file: " + fileName, e);
        }
    }
}