# NetChat – Java‑Based Network Chatting Application  
**Repository:** `github.com/your‑org/NetChat`  
**Description:** A Java based chatting application that can be used to communicate with other systems on a network.

---

## Table of Contents
1. [Prerequisites](#prerequisites)  
2. [Installation](#installation)  
3. [Quick‑Start Usage](#quick-start-usage)  
4. [Configuration](#configuration)  
5. [API Documentation](#api-documentation)  
6. [Code Examples](#code-examples)  
7. [Testing & Debugging](#testing--debugging)  
8. [Contributing](#contributing)  
9. [License](#license)  

---

## Prerequisites
| Tool | Minimum Version | Why |
|------|----------------|-----|
| **Java Development Kit (JDK)** | 11 (or newer) | Language runtime & compiler |
| **Maven** (or Gradle) | 3.6+ | Build & dependency management |
| **Git** | any | Clone the repository |
| **Network** | TCP/IP reachable between peers | Required for socket communication |
| **Optional** | Docker 20+ | Run NetChat in a container for CI or sandboxed testing |

> **Tip:** NetChat is pure Java – no native libraries are required. It works on any OS that can run a JDK (Linux, macOS, Windows).

---

## Installation

### 1. Clone the repository
```bash
git clone https://github.com/your-org/NetChat.git
cd NetChat
```

### 2. Build the project

#### Using Maven (default)
```bash
# Compile, run tests, and package a runnable JAR
mvn clean package
```

The resulting artifact will be located at:
```
target/netchat-<version>-jar-with-dependencies.jar
```

#### Using Gradle (if you prefer)
```bash
./gradlew clean build
```
The JAR will be in `build/libs/`.

### 3. (Optional) Install locally to your Maven repository
```bash
mvn install
```
This makes `netchat` available as a dependency for other Maven projects:
```xml
<dependency>
    <groupId>org.yourorg</groupId>
    <artifactId>netchat</artifactId>
    <version>${netchat.version}</version>
</dependency>
```

### 4. Docker image (for CI / containerised deployment)
```bash
docker build -t netchat:latest .
# Run a server container
docker run -d -p 5000:5000 --name netchat-server netchat:latest server
```

---

## Quick‑Start Usage

NetChat ships with two command‑line entry points:

| Mode | Command | Description |
|------|---------|-------------|
| **Server** | `java -jar netchat-*.jar server [options]` | Starts a chat server that accepts client connections. |
| **Client** | `java -jar netchat-*.jar client [options]` | Starts an interactive client that connects to a server. |

### 1. Start a Server
```bash
java -jar target/netchat-1.2.0-jar-with-dependencies.jar server \
    --port 5000 \
    --max-clients 50 \
    --log-level INFO
```

**Common server options**

| Option | Short | Default | Description |
|--------|-------|---------|-------------|
| `--port` | `-p` | `5000` | TCP port the server listens on |
| `--max-clients` | `-m` | `100` | Maximum simultaneous connections |
| `--log-level` | `-l` | `INFO` | Logging verbosity (`TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`) |
| `--auth-file` | `-a` | none | Path to a JSON file with username/password pairs (optional) |

### 2. Connect a Client
```bash
java -jar target/netchat-1.2.0-jar-with-dependencies.jar client \
    --host 192.168.1.10 \
    --port 5000 \
    --username alice \
    --password secret
```

**Common client options**

| Option | Short | Default | Description |
|--------|-------|---------|-------------|
| `--host` | `-h` | `localhost` | Server IP/hostname |
| `--port` | `-p` | `5000` | Server port |
| `--username` | `-u` | none | Login name (required if auth is enabled) |
| `--password` | `-w` | none | Password (required if auth is enabled) |
| `--reconnect` | `-r` | `false` | Auto‑reconnect on disconnect |
| `--history` | `-H` | `100` | Number of recent messages to keep locally |

Once connected, you can type messages directly into the console. Use the following **slash commands** for extra functionality:

| Command | Syntax | Description |
|---------|--------|-------------|
| `/quit` | `/quit` | Gracefully disconnect and exit |
| `/list` | `/list` | Show a list of currently connected users |
| `/msg` | `/msg <user> <text>` | Send a private message |
| `/nick` | `/nick <newName>` | Change your display name |
| `/help` | `/help` | Show the command list |

---

## Configuration

NetChat can be configured via **YAML**, **JSON**, or **Java properties** files. The default location is `config/netchat.yml` (or `netchat.json`).

### Example `netchat.yml`
```yaml
server:
  port: 5000
  maxClients: 200
  authFile: "config/users.json"
  tls:
    enabled: true
    keystore: "certs/keystore.jks"
    keystorePassword: "changeit"

client:
  reconnect: true
  historySize: 200
  ui:
    theme: "dark"
    timestampFormat: "HH:mm:ss"

logging:
  level: INFO
  file: "logs/netchat.log"
```

### Authentication file (`users.json`)
```json
{
  "users": [
    {"username": "alice", "passwordHash": "5e884898da..."},
    {"username": "bob",   "passwordHash": "2c1743a391..."}
  ]
}
```
> **Security note:** Passwords are stored as **bcrypt** hashes. Use the provided `PasswordUtil` class to generate hashes.

### TLS / SSL
If `tls.enabled` is true, NetChat will wrap the socket in an `SSLSocket`. Provide a Java Keystore (`.jks`) containing a server certificate signed by a trusted CA (or a self‑signed cert for testing).

```bash
# Generate a self‑signed keystore (valid for 365 days)
keytool -genkeypair -alias netchat \
    -keyalg RSA -keysize 2048 \
    -validity 365 \
    -keystore certs/keystore.jks \
    -storepass changeit \
    -dname "CN=NetChat Server, OU=Dev, O=YourOrg, L=City, S=State, C=US"
```

---

## API Documentation

> The API is packaged under the base namespace `org.yourorg.netchat`.  
> Javadoc can be generated with `mvn javadoc:javadoc` (output in `target/site/apidocs`).

Below is a concise reference for the most important public classes and interfaces.

### 1. Core Classes

| Class | Package | Description |
|-------|---------|-------------|
| **`NetChatServer`** | `org.yourorg.netchat.server` | Main server implementation. Handles client connections, broadcasting, private messaging, and optional authentication. |
| **`NetChatClient`** | `org.yourorg.netchat.client` | High‑level client API. Provides methods to connect, send messages, register listeners, and disconnect. |
| **`Message`** | `org.yourorg.netchat.model` | Immutable data class representing a chat message (sender, timestamp, payload, optional target). |
| **`User`** | `org.yourorg.netchat.model` | Represents a connected user (username, display name, connection state). |
| **`ChatListener`** | `org.yourorg.netchat.listener` | Callback interface for receiving events (message received, user joined/left, errors). |
| **`AuthProvider`** | `org.yourorg.netchat.auth` | Interface for pluggable authentication mechanisms (file‑based, LDAP, OAuth). |
| **`PasswordUtil`** | `org.yourorg.netchat.util` | Static helpers for hashing/validating passwords (bcrypt). |
