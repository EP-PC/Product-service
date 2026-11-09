package store.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Regras de autorizacao do Product (Fase 2), a partir dos headers que o Gateway injeta.
 *
 * Premissa: so o Gateway e exposto para fora, e ele
 *  - apaga id-account/id-role enviados pelo cliente;
 *  - injeta id-account e id-role em toda rota protegida, depois de validar o JWT.
 *
 * Diferente do Account, aqui nao existe "chamada interna" liberada: ninguem interno
 * escreve produtos (o Order so le). Escrita exige id-role ADMIN, sempre.
 *
 * O header e lido aqui, e nao na interface ProductController, porque o contrato
 * store:product esta congelado (o Order compila contra ele).
 */
@Component
public class ProductAuthorization {

    public static final String HEADER_ID_ROLE = "id-role";
    public static final String ROLE_ADMIN = "ADMIN";

    // o Spring injeta um proxy que aponta para o request da thread atual,
    // entao e seguro usar num bean singleton
    @Autowired
    private HttpServletRequest request;

    // criar/apagar produto: so ADMIN; USER ou sem header -> 403
    public void requireAdmin() {
        if (!ROLE_ADMIN.equals(header(HEADER_ID_ROLE))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }

    // header vazio conta como ausente
    private String header(String name) {
        String value = request.getHeader(name);
        return value == null || value.isBlank() ? null : value.trim();
    }

}
