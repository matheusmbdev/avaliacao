package com.sicredi.avaliacao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sicredi.avaliacao.api.cloud.feignclient.UserInfoClient;
import com.sicredi.avaliacao.api.v1.model.AssociadoCpf;
import com.sicredi.avaliacao.domain.enums.StatusCpf;
import com.sicredi.avaliacao.domain.model.Pauta;
import com.sicredi.avaliacao.domain.model.Sessao;
import com.sicredi.avaliacao.domain.model.Voto;
import com.sicredi.avaliacao.domain.repositories.PautaRepository;
import com.sicredi.avaliacao.domain.repositories.SessaoRepository;
import com.sicredi.avaliacao.util.DatabaseCleaner;
import com.sicredi.avaliacao.util.ResourceUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yaml"})
class PautaIT {

    private static final UUID PAUTA_ID_INEXISTENTE = UUID.randomUUID();
    public static final String JSON_CORRETO_PAUTA = "/json/correto/pauta.json";
    public static final String JSON_INCORRETO_PAUTA = "/json/incorreto/pautaIncorreta.json";
    public static final String JSON_CORRETO_VOTO = "/json/correto/voto.json";
    public static final String JSON_INCORRETO_VOTO = "/json/incorreto/votoIncorreta.json";
    public static final String SESSAO_DE_VOTACAO_INICIADA = "Sess??o de vota????o iniciada.";
    public static final String VOTO_CONTABILIZADO_COM_SUCESSO = "Voto contabilizado com sucesso!";
    public static final String ASSOCIADO_ILEGIVEL_PARA_VOTACAO_NESTA_PAUTA = "Associado ileg??vel para vota????o nesta Pauta.";

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private PautaRepository pautaRepository;

    @Autowired
    private SessaoRepository sessaoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserInfoClient userInfoClient;

    @LocalServerPort
    private int port;

    private Pauta primeiraPauta;

    private Voto voto;

    private AssociadoCpf associadoCpf;

    private int quantidadeDePautasCadastradas;

    private String jsonCorretoPauta;
    private String jsonIncorretoPauta;
    private String jsonCorretoVoto;
    private String jsonIncorretoVoto;

    @BeforeEach
    void setUp() throws Exception {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = "/v1/pauta";

        databaseCleaner.clearTables();
        prepararDados();

        jsonCorretoPauta = ResourceUtils.getContentFromResource(JSON_CORRETO_PAUTA);
        jsonIncorretoPauta = ResourceUtils.getContentFromResource(JSON_INCORRETO_PAUTA);
        jsonCorretoVoto = ResourceUtils.getContentFromResource(JSON_CORRETO_VOTO);
        jsonIncorretoVoto = ResourceUtils.getContentFromResource(JSON_INCORRETO_VOTO);

        voto = objectMapper.readValue(jsonCorretoVoto, Voto.class);
        associadoCpf = new AssociadoCpf(StatusCpf.ABLE_TO_VOTE);
    }

    @Test
    void deveRetornarStatus200_QuandoConsultarPautas() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    void deveRetornarQuantidadeCorretaDePautas_QuandoConsultarPautas() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get()
        .then()
            .body("content", hasSize(quantidadeDePautasCadastradas));
    }

    @Test
    void deveRetornarStatus201_QuandoCadastrarPauta() {
        given()
            .body(jsonCorretoPauta)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("tema", equalTo("PEC 0001"));
    }

    @Test
    void deveRetornarStatus400_QuandoCadastrarPautaComCorpoDaRequisicaoIncorreta() {
        given()
            .body(jsonIncorretoPauta)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void deveRetornarRespotaEStatusCorretos_QuandoConsultarPautaExistente() {
        given()
            .pathParam("pautaId", primeiraPauta.getPautaId())
            .accept(ContentType.JSON)
        .when()
            .get("/{pautaId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("tema", equalTo(primeiraPauta.getTema()));
    }

    @Test
    void deveRetornarStatus404_QuandoConsultarPautaInexistente() {
        given()
            .pathParam("pautaId", PAUTA_ID_INEXISTENTE)
            .accept(ContentType.JSON)
        .when()
            .get("/{pautaId}")
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void deveAbrirSessaoDeVotacaoDaPauta_QuandoAbrirSessaoEmPautaExistente() {
        String result = given()
                            .pathParam("pautaId", primeiraPauta.getPautaId())
                            .accept(ContentType.JSON)
                        .when()
                            .post("/{pautaId}/abrir-sessao")
                        .then()
                            .statusCode(HttpStatus.CREATED.value())
                            .extract().asString();

        Assertions.assertEquals(SESSAO_DE_VOTACAO_INICIADA, result);
    }

    @Test
    void deveRetornarStatus404_QuandoAbrirSessaoEmPautaInexistente() {
        given()
            .pathParam("pautaId", UUID.randomUUID())
            .accept(ContentType.JSON)
        .when()
            .post("/{pautaId}/abrir-sessao")
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void deveVotarEmUmaPauta_QuandoVotarEmPautaExistente() {
        when(userInfoClient.checarCpf(sanitizarCpf(voto.getCpfAssociado()))).thenReturn(associadoCpf);
        Sessao sessao = new Sessao(primeiraPauta);
        sessaoRepository.save(sessao);

        String result = given()
                            .body(jsonCorretoVoto)
                            .contentType(ContentType.JSON)
                            .pathParam("pautaId", primeiraPauta.getPautaId())
                            .accept(ContentType.JSON)
                        .when()
                            .post("/{pautaId}/votar")
                        .then()
                            .statusCode(HttpStatus.OK.value())
                            .extract().asString();

        Assertions.assertEquals(VOTO_CONTABILIZADO_COM_SUCESSO, result);
    }

    @Test
    void deveRetornarStatus400_QuandoVotarEmPautaExistenteMasNaoSerLegivel() {
        associadoCpf.setStatus(StatusCpf.UNABLE_TO_VOTE);
        when(userInfoClient.checarCpf(sanitizarCpf(voto.getCpfAssociado()))).thenReturn(associadoCpf);
        Sessao sessao = new Sessao(primeiraPauta);
        sessaoRepository.save(sessao);

        given()
            .body(jsonCorretoVoto)
            .contentType(ContentType.JSON)
            .pathParam("pautaId", primeiraPauta.getPautaId())
            .accept(ContentType.JSON)
        .when()
            .post("/{pautaId}/votar")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("detail", equalTo(ASSOCIADO_ILEGIVEL_PARA_VOTACAO_NESTA_PAUTA));

    }

    @Test
    void deveRetornarStatus400_QuandoVotarEmPautaComCorpoDaRequisicaoIncorreta() {
        given()
            .body(jsonIncorretoVoto)
            .contentType(ContentType.JSON)
            .pathParam("pautaId", primeiraPauta.getPautaId())
            .accept(ContentType.JSON)
        .when()
            .post("/{pautaId}/votar")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());

    }

    private void prepararDados() {
        Pauta primeiraPauta = new Pauta();
        primeiraPauta.setTema("PEC 9999");
        this.primeiraPauta = pautaRepository.save(primeiraPauta);

        Pauta segundaPauta = new Pauta();
        segundaPauta.setTema("PEC 8888");
        pautaRepository.save(segundaPauta);

        this.quantidadeDePautasCadastradas = (int) pautaRepository.count();
    }

    private String sanitizarCpf(String cpfAssociado) {
        return cpfAssociado.replace(".", "")
                .replace("-", "");
    }
}

