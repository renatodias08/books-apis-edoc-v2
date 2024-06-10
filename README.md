# Docker Compose books-apis-edoc

## Execute o sistema
Podemos executar tudo facilmente com apenas um único comando:
```bash
docker compose up
```

O Docker irá extrair as imagens MySQL e Spring Boot (se nossa máquina não as tiver antes).

Os serviços podem ser executados em segundo plano com o comando:
```bash
docker compose up -d
```

## Pare o sistema
Parar todos os contêineres em execução também é simples com um único comando:
```bash
docker compose down
```

Se você precisar parar e remover todos os contêineres, redes e todas as imagens usadas por qualquer serviço no arquivo <em>docker-compose.yml</em>, use o comando:
```bash
docker compose down --rmi all
```
