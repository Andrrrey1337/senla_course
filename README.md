## Запуск проекта

Для запуска всей инфраструктуры требуется только Docker

1. Склонируйте репозиторий и перейдите в корневую папку проекта
2. Выполните команду для сборки Java-приложений и старта всех сервисов:
   ```bash
   docker-compose up -d --remove-orphans
   docker-compose logs -f app-producer app-consumer