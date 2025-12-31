package com.example;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class AppOpenAI {
    public static void main(String[] args) {
        Scanner userinput;      // user inputted line as a Scanner
        String cmdline;

        // Get API key from environment variable
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("Error: OPENAI_API_KEY environment variable not set");
            System.err.println("Please set it with: export OPENAI_API_KEY='your-api-key'");
            System.exit(1);
        }

        ChatModel cmodel = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gpt-4o-mini")  // or "gpt-4", "gpt-3.5-turbo"
                .temperature(0.2)
                .timeout(java.time.Duration.ofMinutes(2))
                .build();

        ChatMemory cm = MessageWindowChatMemory.withMaxMessages(20);

        // Load context once - this won't be in chat memory, but sent with first system message
        String context = readResourceFile("mr_robot_wiki.txt");
        
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
        try (InputStream is = AppOpenAI.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new RuntimeException("Resource file not found: " + fileName);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource file: " + fileName, e);
        }
    }
}