# HConex

**HConex** (Habbo + Conexión) is a Habbo Hotel interceptor proxy application for [habbo.es](https://www.habbo.es), inspired by [G-Earth](https://github.com/sirjonasxx/G-Earth).

## Overview

HConex acts as a TCP proxy between the Habbo Hotel client and the habbo.es servers, allowing you to:

- **Intercept packets** sent and received by the game client
- **Log and analyze** client-server communication in real time
- **Build extensions** to automate or modify game behavior
- **Inspect the Habbo protocol** with a user-friendly JavaFX desktop UI

## Version

`0.0.1` — Initial project structure (work in progress)

## Tech Stack

| Technology | Purpose |
|------------|---------|
| **Java 11+** | Core language |
| **JavaFX 21** | Desktop UI |
| **Netty 4.1** | High-performance TCP networking |
| **Log4j2** | Structured logging |
| **JUnit 5** | Unit testing |

## Project Structure

```
src/main/java/com/hconex/
├── HConexApplication.java          # JavaFX entry point
├── config/
│   └── HabboConfig.java            # habbo.es server configuration
├── core/
│   ├── proxy/
│   │   ├── ProxyServer.java        # Netty TCP proxy server
│   │   ├── ProxyHandler.java       # Channel handler (bidirectional)
│   │   └── ConnectionManager.java  # Active connection tracking
│   ├── protocol/
│   │   ├── HabboProtocol.java      # Packet parser
│   │   ├── PacketFactory.java      # Packet instantiation
│   │   └── PacketEncoder.java      # Packet serialization
│   ├── packets/
│   │   ├── Packet.java             # Base packet class
│   │   ├── incoming/               # Server → Client packets
│   │   └── outgoing/               # Client → Server packets
│   └── crypto/
│       ├── Encryption.java         # Encryption utilities
│       └── RC4Cipher.java          # RC4 stream cipher
├── ui/
│   ├── controllers/
│   │   ├── MainController.java
│   │   ├── PacketLogController.java
│   │   └── ConnectionController.java
│   └── models/
│       ├── PacketViewModel.java
│       └── ConnectionViewModel.java
├── extensions/
│   ├── Extension.java              # Extension interface
│   ├── ExtensionAPI.java           # API exposed to extensions
│   └── ExtensionLoader.java        # Dynamic extension loader
├── logging/
│   ├── PacketLogger.java           # In-memory packet log
│   └── LogEntry.java               # Single log entry
└── utils/
    ├── HexUtils.java               # Hex encoding/decoding
    └── ByteUtils.java              # Byte manipulation helpers
```

## Prerequisites

- **Java 11** or higher
- **Maven 3.6+**

## Build & Run

```bash
# Clone the repository
git clone https://github.com/Bastiian787/HConex.git
cd HConex

# Build the project
mvn clean compile

# Run tests
mvn test

# Launch the application
mvn javafx:run
```

## Configuration

Server settings are defined in `HabboConfig.java`:

| Property | Default |
|----------|---------|
| Server host | `habbo.es` |
| Server port | `30000` |
| Local proxy port | `8080` |

## How It Works

1. HConex starts a local Netty TCP server on port `8080`.
2. The Habbo client connects to `localhost:8080` instead of `habbo.es:30000`.
3. HConex forwards all traffic between client and server bidirectionally.
4. Every packet is parsed, logged, and dispatched to registered extensions.

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License

This project is open source and available under the [MIT License](LICENSE).
