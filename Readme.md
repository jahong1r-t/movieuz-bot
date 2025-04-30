# Movieuz Bot

![Movieuz Bot](https://img.shields.io/badge/Bot-Telegram-blue.svg)
![Java](https://img.shields.io/badge/Language-Java-orange.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)

`Movieuz` is a Telegram bot designed to manage and share movies, enabling users to search, download, and rate movies while providing administrators with tools to manage content and monitor statistics. The bot is built with Java and integrates with a PostgreSQL database for efficient data management.

## Features
- **User Features**:
    - Search movies by code.
    - Download movies shared via Telegram.
    - Rate movies on a 1-5 star scale.
    - Receive notifications about new movies and channels.

- **Admin Features**:
    - Add, edit, or delete movies.
    - Manage Telegram channels linked to the bot.
    - Broadcast messages to all users (text, photos, videos, etc.).
    - View detailed statistics (e.g., total users, active users, top downloaded movies).

## Technologies
- **Programming Language**: Java 17
- **Database**: PostgreSQL
- **Dependencies**:
    - Lombok (for reducing boilerplate code)
    - SLF4J (for logging, planned)
    - Telegram Bot API (via `telegrambots` library)
- **Build Tool**: Maven
- **Configuration**: Environment variables via `.env` file

## Contributing
Contributions are welcome! To contribute:
1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature`).
3. Make your changes and commit (`git commit -m "Add your feature"`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Open a Pull Request.

Please ensure your code follows the existing style and includes tests where applicable.

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

For any questions or support, contact the bot admin via Telegram or open an issue on GitHub.
