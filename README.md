# Hello AI Friend - Mr. Robot Chatbot 🤖

An intelligent chatbot that answers questions about the Mr. Robot TV series using AI models. This project demonstrates how to build a context-aware chatbot using either local LLMs (via Ollama) or cloud-based models (OpenAI).

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Data Gathering](#data-gathering)
- [Ollama Setup](#ollama-setup)
- [Running the Application](#running-the-application)
- [Project Structure](#project-structure)
- [Technical Details](#technical-details)

## 🎯 Overview

This project implements a conversational AI chatbot specialized in answering questions about the Mr. Robot TV series. It includes:

- **Data gathering scripts** to collect information from Wikipedia and episode summaries
- **Two implementations**: one using local Ollama models and one using OpenAI API
- **Chat memory** to maintain conversation context
- **Episode-specific knowledge** from all seasons

## ✨ Features

- 💬 Interactive command-line chat interface
- 🧠 Context-aware responses using episode data
- 💾 Conversation memory (maintains last 20 messages)
- 🔄 Support for both local (Ollama) and cloud (OpenAI) models
- 📚 Comprehensive knowledge base from Wikipedia and episode summaries

## 🔧 Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **Python 3.7+** (for data gathering scripts)
- **Ollama** (for local model) OR **OpenAI API key** (for cloud model)

## 📊 Data Gathering

The project includes three Python scripts to collect Mr. Robot data:

### 1. Fetch Wikipedia Data

```bash
cd data
python fetch_mr_robot_wiki.py
```

**Purpose**: Downloads the Mr. Robot Wikipedia page content and saves it to `mr_robot_wiki.txt`.

**Output**: 
- `mr_robot_wiki.txt` - Complete Wikipedia article about the TV series

### 2. Fetch Episode Summary

```bash
python fetch_episode_summary.py <episode_url> [output_file]
```

**Purpose**: Fetches a single episode summary from the Mr. Robot Fandom wiki.

**Example**:
```bash
python fetch_episode_summary.py "https://mrrobot.fandom.com/wiki/eps1.0_hellofriend.mov/Summary"
```

### 3. Fetch All Episodes

```bash
python fetch_all_episodes.py [csv_file] [output_file]
```

**Purpose**: Fetches all episode summaries from the `season_episodes.csv` file and combines them into a single text file.

**Features**:
- Reads episode URLs from CSV
- Polite request delays (1 second between requests)
- Progress tracking and error handling
- Statistics reporting

**Output**: 
- `mr_robot_all_episodes_summary.txt` - All episode summaries in one file

**Dependencies**:
```bash
pip install requests beautifulsoup4 wikipedia-api
```

## 🚀 Ollama Setup

Ollama allows you to run large language models locally on your machine.

### Step 1: Install Ollama and Pull Base Model

```bash
cd ollama
chmod +x install-ollama.sh
./install-ollama.sh
```

This script:
1. Installs Ollama (if not already installed)
2. Starts the Ollama server
3. Downloads the `qwen2.5:7b` base model

### Step 2: Create Custom Model

The `ModelFile` defines a customized model with specific parameters:

```bash
ollama create mr_robot_v2 -f ModelFile
```

**ModelFile Configuration**:
- **Base model**: `qwen2.5:7b` (7 billion parameter model)
- **Temperature**: 0.5 (moderate creativity)
- **Context size**: 8192 tokens (handles large Wikipedia content)
- **Seed**: 42 (for reproducible results)

### Step 3: Verify Model

```bash
ollama list
```

You should see `mr_robot_v2` in the list.

### Step 4: Start Ollama Server

If not already running:

```bash
ollama serve
```

Keep this running in a separate terminal while using the application.

## 🏃 Running the Application

### Option 1: Using Ollama (Local Model)

1. **Ensure Ollama is running**:
   ```bash
   ollama serve
   ```

2. **Run the application**:
   ```bash
   mvn exec:java -Dexec.mainClass="com.example.App"
   ```

3. **Start chatting**:
   ```
   prompt> Who is Elliot Alderson?
   prompt> What happens in season 1?
   prompt> Tell me about fsociety
   ```

### Option 2: Using OpenAI API

1. **Set your OpenAI API key**:
   ```bash
   export OPENAI_API_KEY='your-api-key-here'
   ```

2. **Run the application**:
   ```bash
   mvn exec:java -Dexec.mainClass="com.example.AppOpenAI"
   ```

3. **Start chatting** (same interface as Ollama version)

### Building the Project

```bash
mvn clean compile
```

### Running Tests

```bash
mvn test
```

## 📁 Project Structure

```
hello-ai-friend/
├── data/                                    # Data collection scripts
│   ├── fetch_all_episodes.py              # Fetch all episode summaries
│   ├── fetch_episode_summary.py           # Fetch single episode
│   ├── fetch_mr_robot_wiki.py            # Fetch Wikipedia data
│   ├── season_episodes.csv                # Episode URL list
│   ├── mr_robot_wiki.txt                  # Wikipedia content
│   └── mr_robot_all_episodes_summary.txt  # All episodes
├── ollama/                                  # Ollama configuration
│   ├── install-ollama.sh                  # Installation script
│   └── ModelFile                          # Custom model definition
├── src/main/java/com/example/
│   ├── App.java                           # Ollama version
│   └── AppOpenAI.java                     # OpenAI version
├── src/main/resources/
│   └── mr_robot_wiki.txt                  # Context data for chatbot
└── pom.xml                                 # Maven configuration
```

## 🔍 Technical Details

### App.java (Ollama Version)

**Key Components**:

1. **Model Configuration**:
   ```java
   ChatModel cmodel = OllamaChatModel.builder()
       .baseUrl("http://localhost:11434")    // Local Ollama server
       .modelName("mr_robot_v2")             // Custom model
       .temperature(0.2)                      // Low temperature for factual answers
       .timeout(Duration.ofMinutes(2))       // 2-minute timeout
       .build();
   ```

2. **Chat Memory**:
   ```java
   ChatMemory cm = MessageWindowChatMemory.withMaxMessages(20);
   ```
   - Maintains last 20 messages for context
   - Enables follow-up questions and conversational flow

3. **System Context**:
   - Loads `mr_robot_wiki.txt` once at startup
   - Injected as system message with role definition
   - Provides episode-specific knowledge to the AI

4. **Chat Loop**:
   - Reads user input from command line
   - Adds user message to memory
   - Sends all messages (including context) to model
   - Displays AI response
   - Adds AI response to memory for context

**Why Ollama?**
- ✅ Runs completely offline
- ✅ Free to use
- ✅ Privacy - your data stays local
- ✅ No API rate limits
- ❌ Requires decent hardware (GPU recommended)
- ❌ Slower than cloud APIs

### AppOpenAI.java (OpenAI Version)

**Key Differences from App.java**:

1. **API Key Requirement**:
   ```java
   String apiKey = System.getenv("OPENAI_API_KEY");
   ```
   - Reads from environment variable
   - Exits with error message if not set

2. **Model Configuration**:
   ```java
   ChatModel cmodel = OpenAiChatModel.builder()
       .apiKey(apiKey)
       .modelName("gpt-4o-mini")             // OpenAI model
       .temperature(0.2)
       .timeout(Duration.ofMinutes(2))
       .build();
   ```

3. **Same Architecture**:
   - Identical chat memory system
   - Same context loading mechanism
   - Same user interaction loop
   - Same resource file reading

**Why OpenAI?**
- ✅ Very fast responses
- ✅ High-quality answers
- ✅ Works on any hardware
- ✅ No local setup required
- ❌ Costs money (pay per token)
- ❌ Requires internet connection
- ❌ Data sent to external service

**Model Options**:
- `gpt-4o-mini` - Fast and cost-effective (default)
- `gpt-4o` - Most capable, more expensive
- `gpt-3.5-turbo` - Older, cheaper option

### LangChain4j Framework

Both applications use [LangChain4j](https://github.com/langchain4j/langchain4j), a Java framework for building LLM applications:

**Dependencies** (from pom.xml):
- `langchain4j` - Core framework
- `langchain4j-ollama` - Ollama integration
- `langchain4j-open-ai` - OpenAI integration
- Also includes: Google AI, Anthropic, Mistral AI support

**Key Concepts**:
- **ChatModel**: Abstraction for different AI providers
- **ChatMemory**: Manages conversation history
- **Messages**: SystemMessage, UserMessage, AiMessage
- **Provider-agnostic**: Easy to switch between models

### Resource Loading

Both apps load context from `src/main/resources/mr_robot_wiki.txt`:

```java
private static String readResourceFile(String fileName) {
    try (InputStream is = App.class.getClassLoader()
            .getResourceAsStream(fileName)) {
        if (is == null) {
            throw new RuntimeException("Resource file not found: " + fileName);
        }
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
        throw new RuntimeException("Failed to read resource file", e);
    }
}
```

**Benefits**:
- Resources are bundled in JAR
- No external file dependencies at runtime
- Cross-platform compatibility

## 🎓 Example Usage

```bash
$ mvn exec:java -Dexec.mainClass="com.example.App"

prompt> Who is the main character?
The main character is Elliot Alderson, a cybersecurity engineer and hacker...

prompt> What is fsociety?
fsociety is a hacktivist group led by Mr. Robot. They aim to take down E Corp...

prompt> Tell me about season 4
Season 4 is the final season of Mr. Robot, consisting of 13 episodes...
```

## 📝 License

This project is for educational purposes. Mr. Robot is property of USA Network.

## 🤝 Contributing

Feel free to submit issues and enhancement requests!

## 🙏 Acknowledgments

- Mr. Robot TV series for the inspiration
- LangChain4j for the excellent Java LLM framework
- Ollama for making local LLMs accessible
- OpenAI for their powerful API