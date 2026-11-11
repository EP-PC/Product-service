# Product-service

Implementação do microserviço **Product** da store (contrato em `store:product`).

## Rodando localmente

```bash
mvn clean package
docker compose up --build
```

O `compose.yaml` sobe um Postgres e o product na porta 8080. O Flyway cria o schema
`products` e a tabela na primeira subida.

## API

Base `/products`. IDs são UUID e o preço é `BigDecimal`.

| Endpoint | Sucesso | Erros |
|---|---|---|
| `POST /products` | 201 + `Location` + body | 400 (validação), 403 |
| `GET /products` | 200 + lista | — |
| `GET /products?name=` | 200 + lista (busca parcial, fora do contrato) | — |
| `GET /products/{id}` | 200 | 404 |
| `DELETE /products/{id}` | 204 (inclusive id desconhecido) | 403 |
| `GET /products/health-check` | 200 | — |

## Autorização (headers do gateway)

O gateway valida o JWT e injeta `id-account` e `id-role` (`USER`/`ADMIN`) em toda rota
protegida, apagando os que o cliente tenta enviar. Os `GET /products/**` são rotas abertas
no gateway (vitrine pública) e chegam sem esses headers.

| Endpoint | `ADMIN` | `USER` | Sem `id-role` |
|---|---|---|---|
| `POST /products`, `DELETE /products/{id}` | permitido | 403 | 403 |
| `GET /products`, `GET /products/{id}`, `health-check` | permitido | permitido | permitido |

Diferente do Account, aqui não existe exceção para "chamada interna": nenhum serviço interno
escreve produtos (o Order só consulta). A checagem vem antes da validação, então um `USER`
com body inválido recebe 403, não 400. Negado = **403** com `"Access denied"`; o 401 (sem
login) é responsabilidade do gateway.

## Kubernetes (Minikube)

Manifestos em `k8s/k8s.yaml` (Deployment + Service `ClusterIP` 8080, nome `product`).
Pré-requisito: o Postgres do repo root já aplicado (`postgres-configmap`, `postgres-secrets`
e Service `postgres`). O product não tem segredo próprio: usa só as credenciais do banco.

Imagem (o cluster puxa do Docker Hub; a tag é o commit do código):

```bash
mvn package
docker build -t pedrohmm1/product:<tag> -t pedrohmm1/product:latest .
docker push pedrohmm1/product:<tag>
docker push pedrohmm1/product:latest
```

Deploy e teste (o Service é interno; para testar de fora, use port-forward):

```bash
kubectl apply -f k8s/k8s.yaml
kubectl get pods,svc
kubectl logs deploy/product
kubectl port-forward svc/product 8081:8080   # http://localhost:8081/products
```

Sem gateway, os headers precisam ser enviados à mão para testar a escrita:

```bash
curl -i -X POST http://localhost:8081/products \
  -H 'Content-Type: application/json' \
  -H 'id-account: 00000000-0000-0000-0000-000000000001' -H 'id-role: ADMIN' \
  -d '{"name":"Teste","price":9.90,"unit":"un"}'
```

A JVM usa até 50% do limite de memória do container como heap
(`JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=50`), para não estourar os 512Mi.
