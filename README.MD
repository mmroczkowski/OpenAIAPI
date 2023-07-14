# Spring OpenAI API Client

This Java library is an easy-to-use client for interacting with OpenAI's API within a Spring application. The library offers methods for interacting with the Chat and Embeddings endpoints.

## Installation

To install this package into your project, you can include it as a Maven dependency. Here is the configuration for `pom.xml`:

```xml
<dependency>
    <groupId>ai.optfor</groupId>
    <artifactId>spring-openai-api</artifactId>
    <version>0.1</version>
</dependency>
```

## Features

- Easy to use: The API methods are directly available from the `OpenAIApi` class. No need to deal with HTTP requests and responses.
- Authorization handling: The API key is used in all requests as a Bearer Token.

## Usage

### Instantiation

Instantiate the `OpenAIApi` class by passing your OpenAI key.

```java
OpenAIApi openaiApi = new OpenAIApi("your_openai_key");
```

### Chat Completion

Use the `chatCompletion` method to generate chat completions.

```java
ChatCompletionResponse response = openaiApi.chatCompletion("gpt-3.5-turbo", "Hello, how are you?", "system", 60);
```

### Embeddings

Use the `embedding` method to generate embeddings.

```java
EmbeddingData embeddingData = openaiApi.embedding("gpt-3.5-turbo", "Hello, how are you?");
```

## Dependencies

- Java 18
- Spring Web 6.0.10

## Contributing

We welcome contributions to this project. If you have an improvement or feature request, please submit a pull request or open an issue on the project's repository.

## License

This library is licensed under the MIT license.