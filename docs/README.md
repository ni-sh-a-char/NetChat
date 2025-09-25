# NetChat – Java‑based Network Chatting Application  
**Version:** 2.3.0 (latest)  
**Repository:** https://github.com/your‑org/NetChat  

> **NetChat** is a lightweight, extensible Java library and command‑line tool that lets you create real‑time text chat sessions between any number of machines on a LAN or over the Internet. It ships with a ready‑to‑run server, a console client, and a clean public API that can be embedded in your own Java projects.

---

## Table of Contents
1. [Installation](#installation)  
2. [Quick‑Start Usage](#quick-start-usage)  
3. [Command‑Line Tools](#command-line-tools)  
4. [API Documentation](#api-documentation)  
5. [Code Examples](#code-examples)  
6. [Configuration Options](#configuration-options)  
7. [Building from Source](#building-from-source)  
8. [Contributing & License](#contributing--license)  

---

## Installation  

NetChat can be used in three ways:

| Method | When to use | Steps |
|--------|-------------|-------|
| **Pre‑built binaries** (recommended for quick testing) | You just want to run the server/client without modifying the code. | 1. Download the latest `netchat‑<version>.zip` from the *Releases* page.<br>2. Unzip to a folder of your choice.<br>3. Ensure you have **Java 17+** installed (`java -version`). |
| **Maven / Gradle dependency** | You are building a Java project and want to embed NetChat as a library. | Add one of the following to your build file: <br>**Maven**<br>```xml\n<dependency>\n    <groupId>io.github.your-org</groupId>\n    <artifactId>netchat</artifactId>\n    <version>2.3.0</version>\n</dependency>\n```<br>**Gradle (Kotlin DSL)**<br>```kotlin\ndependencies {\n    implementation(\"io.github.your-org:netchat:2.3.0\")\n}\n``` |
| **Build from source** | You need to modify the core, contribute, or want the absolute latest commit. | See the **Building from Source** section below. |

### Prerequisites
- **Java Development Kit (JDK) 17** or newer (OpenJDK or Oracle JDK).  
- **Git** (only for source checkout).  
- **Maven 3.8+** (if you are building from source).  

---

## Quick‑Start Usage  

### 1. Start a Chat Server  

```bash
# From the unpacked binary folder
java -jar netchat-server.jar --port 9090 --max-clients 50
```

*Default values*: `--port 8080`, `--max-clients 100`.  
The server prints a log line when it is ready:

```
[INFO] NetChat Server started on 0.0.0.0:9090 (max 50 clients)
```

### 2. Connect a Console Client  

```bash
java -jar netchat-client.jar --host 192.168.1.10 --port 9090 --username Alice
```

You will be dropped into an interactive prompt:

```
Welcome to NetChat! Type '/help' for commands.
> Hello, world!
```

### 3. Basic Commands (client side)

| Command | Description |
|---------|-------------|
| `/help` | Show command list. |
| `/quit` | Disconnect and exit client. |
| `/list` | Request a list of currently connected users. |
| `/msg <user> <text>` | Send a private message. |
| `/nick <newName>` | Change your displayed nickname. |
| `/rooms` | List available chat rooms. |
| `/join <room>` | Join (or create) a chat room. |
| `/leave` | Leave the current room and return to the global lobby. |

---

## Command‑Line Tools  

NetChat ships two executable JARs:

| Tool | Purpose | Main class |
|------|---------|------------|
| `netchat-server.jar` | Runs the central message broker. | `io.netchat.server.ChatServer` |
| `netchat-client.jar` | Simple terminal client for human users. | `io.netchat.client.ConsoleClient` |

Both accept a `--help` flag that prints all supported options.

```bash
java -jar netchat-server.jar --help
java -jar netchat-client.jar --help
```

---

## API Documentation  

> The public API lives in the `io.netchat` package. Below is a concise overview of the most important classes and interfaces. Full Javadoc can be generated with `mvn javadoc:javadoc` or viewed online at https://your-org.github.io/NetChat/javadoc/.

### Core Packages

| Package | Description |
|---------|-------------|
| `io.netchat.core` | Low‑level networking primitives (socket handling, framing, encryption). |
| `io.netchat.server` | Server‑side components (connection manager, room manager, authentication). |
| `io.netchat.client` | Client‑side abstractions (high‑level client API, console UI). |
| `io.netchat.model` | POJOs that represent messages, users, rooms, and events. |
| `io.netchat.api` | Public interfaces that applications should depend on. |

### Key Classes & Interfaces  

| Class / Interface | Brief Description | Important Methods |
|-------------------|-------------------|-------------------|
| **`ChatServer`** (server) | Starts a TCP server, accepts client connections, routes messages. | `start()`, `stop()`, `broadcast(Message)`, `sendPrivate(String userId, Message)` |
| **`ChatClient`** (client) | High‑level client API that can be embedded in GUI or headless apps. | `connect(host, port)`, `login(String username)`, `sendMessage(String text)`, `joinRoom(String room)`, `addListener(ChatEventListener)` |
| **`Message`** (model) | Immutable representation of a chat payload. | `getSender()`, `getContent()`, `isPrivate()`, `getRoom()` |
| **`User`** (model) | Information about a connected participant. | `getId()`, `getNickname()`, `isOnline()` |
| **`ChatRoom`** (model) | Logical grouping of users. | `getName()`, `getMembers()`, `addMember(User)`, `removeMember(User)` |
| **`ChatEventListener`** (api) | Callback interface for asynchronous events. | `onMessageReceived(Message)`, `onUserJoined(User)`, `onUserLeft(User)`, `onError(Throwable)` |
| **`ChatConfig`** (core) | Central configuration holder (port, TLS, max clients, etc.). | `loadFromFile(Path)`, `setPort(int)`, `enableTls(boolean)` |
| **`EncryptionUtil`** (core) | Helper for optional TLS/SSL encryption of the wire. | `wrapSocket(Socket)`, `unwrapSocket(Socket)` |

### Thread‑Safety & Concurrency  

- All public methods of `ChatServer` and `ChatClient` are **thread‑safe**.  
- Internally, NetChat uses a **non‑blocking NIO** selector for the server and a **single‑threaded executor** for each client instance.  
- Event callbacks (`ChatEventListener`) are invoked on a **dedicated event thread**; listeners should return quickly or off‑load work to their own executors.

### Extensibility Points  

| Extension | How to plug in |
|-----------|----------------|
| **Custom Authentication** | Implement `io.netchat.server.AuthProvider` and register via `ChatConfig#setAuthProvider(...)`. |
| **Message Filters / Moderation** | Implement `io.netchat.server.MessageFilter` and add to `ChatServer#addFilter(...)`. |
| **Alternative Transport (WebSocket, UDP)** | Extend `io.netchat.core.Transport` and configure via `ChatConfig#setTransport(...)`. |
| **Persistence (history, offline messages)** | Provide a `MessageStore` implementation and register with `ChatServer#setMessageStore(...)`. |

---

## Code Examples  

### A. Embedding NetChat in a Swing GUI  

```java
import io.netchat.client.ChatClient;
import io.netchat.api.ChatEventListener;
import io.netchat.model.Message;

public class SwingChatWindow extends JFrame implements ChatEventListener {
    private final ChatClient client = new ChatClient();
    private final JTextArea chatArea = new JTextArea();
    private final JTextField inputField = new JTextField();

    public SwingChatWindow() {
        setTitle("NetChat – Swing Demo");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea.set