import org.example.canal.EmailNotificador;
import org.example.canal.NotificadorCanal;
import org.example.canal.SMSNotificador;
import org.example.notificacao.NotificacaoSimples;
import org.example.notificacao.NotificacaoUrgente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Padrão Bridge — Sistema de Notificações")
class NotificacaoTest {

    private EmailNotificador email;
    private SMSNotificador sms;

    @BeforeEach
    void setUp() {
        email = new EmailNotificador("usuario@exemplo.com");
        sms = new SMSNotificador("+55 32 99999-0000");
    }

    @Nested
    @DisplayName("EmailNotificador")
    class EmailNotificadorTest {

        @Test
        @DisplayName("deve retornar 'Email' como identificador do canal")
        void deveRetornarNomeDoCanal() {
            assertEquals("Email", email.getCanal());
        }

        @Test
        @DisplayName("deve registrar a mensagem enviada com destino correto")
        void deveRegistrarMensagemEnviada() {
            email.enviar("Sua fatura está disponível");

            assertEquals(1, email.getMensagensEnviadas().size());
            assertTrue(email.getMensagensEnviadas().get(0).contains("usuario@exemplo.com"));
            assertTrue(email.getMensagensEnviadas().get(0).contains("Sua fatura está disponível"));
        }

        @Test
        @DisplayName("deve acumular múltiplos envios no histórico")
        void deveAcumularMultiplosEnvios() {
            email.enviar("Mensagem 1");
            email.enviar("Mensagem 2");
            email.enviar("Mensagem 3");

            assertEquals(3, email.getMensagensEnviadas().size());
        }
    }

    @Nested
    @DisplayName("SMSNotificador")
    class SMSNotificadorTest {

        @Test
        @DisplayName("deve retornar 'SMS' como identificador do canal")
        void deveRetornarNomeDoCanal() {
            assertEquals("SMS", sms.getCanal());
        }

        @Test
        @DisplayName("deve truncar mensagens maiores que 160 caracteres")
        void deveTruncarMensagemLonga() {
            String longa = "A".repeat(200);
            sms.enviar(longa);

            String registrada = sms.getMensagensEnviadas().get(0);
            assertTrue(registrada.endsWith("..."),
                    "Mensagem longa deve terminar com '...'");
        }

        @Test
        @DisplayName("não deve truncar mensagens com até 160 caracteres")
        void naoDeveTruncarMensagemCurta() {
            String curta = "Mensagem dentro do limite";
            sms.enviar(curta);

            String registrada = sms.getMensagensEnviadas().get(0);
            assertFalse(registrada.endsWith("..."));
            assertTrue(registrada.contains(curta));
        }
    }

    @Nested
    @DisplayName("NotificacaoSimples")
    class NotificacaoSimplesTest {

        @Test
        @DisplayName("deve enviar mensagem formatada com título pelo canal Email")
        void deveEnviarPorEmail() {
            var notif = new NotificacaoSimples("Sistema", email, 1);
            notif.enviar("Deploy concluído com sucesso");

            assertEquals(1, email.getMensagensEnviadas().size());
            String msg = email.getMensagensEnviadas().get(0);
            assertTrue(msg.contains("[Sistema]"));
            assertTrue(msg.contains("Deploy concluído com sucesso"));
        }

        @Test
        @DisplayName("deve enviar mensagem formatada com título pelo canal SMS")
        void deveEnviarPorSMS() {
            var notif = new NotificacaoSimples("Banco", sms, 2);
            notif.enviar("Novo PIX recebido");

            assertEquals(1, sms.getMensagensEnviadas().size());
            String msg = sms.getMensagensEnviadas().get(0);
            assertTrue(msg.contains("[Banco]"));
            assertTrue(msg.contains("Novo PIX recebido"));
        }

        @Test
        @DisplayName("deve retornar o canal correto via getCanal()")
        void deveRetornarCanalCorreto() {
            var notif = new NotificacaoSimples("App", email, 1);
            assertEquals("Email", notif.getCanal().getCanal());
        }

        @Test
        @DisplayName("getInfo() deve conter título, prioridade e canal")
        void getInfoDeveConterDadosCorretos() {
            var notif = new NotificacaoSimples("Alerta", sms, 3);
            String info = notif.getInfo();

            assertTrue(info.contains("Alerta"));
            assertTrue(info.contains("3"));
            assertTrue(info.contains("SMS"));
        }

        @Test
        @DisplayName("deve lançar exceção para prioridade inválida")
        void deveLancarExcecaoParaPrioridadeInvalida() {
            assertThrows(IllegalArgumentException.class,
                    () -> new NotificacaoSimples("X", email, 0));
            assertThrows(IllegalArgumentException.class,
                    () -> new NotificacaoSimples("X", email, 4));
        }

        @Test
        @DisplayName("deve lançar exceção para título vazio")
        void deveLancarExcecaoParaTituloVazio() {
            assertThrows(IllegalArgumentException.class,
                    () -> new NotificacaoSimples("", email, 1));
        }

        @Test
        @DisplayName("deve lançar exceção quando canal for nulo")
        void deveLancarExcecaoParaCanalNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> new NotificacaoSimples("X", null, 1));
        }
    }

    @Nested
    @DisplayName("NotificacaoUrgente")
    class NotificacaoUrgenteTest {

        @Test
        @DisplayName("deve repetir o envio N vezes pelo canal Email")
        void deveRepetirEnvioNVezesPorEmail() {
            var notif = new NotificacaoUrgente("Segurança", email, 3);
            notif.enviar("Tentativa de acesso suspeita detectada");

            assertEquals(3, email.getMensagensEnviadas().size());
        }

        @Test
        @DisplayName("deve repetir o envio N vezes pelo canal SMS")
        void deveRepetirEnvioNVezesPorSMS() {
            var notif = new NotificacaoUrgente("Financeiro", sms, 2);
            notif.enviar("Limite de crédito excedido");

            assertEquals(2, sms.getMensagensEnviadas().size());
        }

        @Test
        @DisplayName("todas as repetições devem conter o prefixo URGENTE")
        void todasRepeticoesDevemConterPreixoUrgente() {
            var notif = new NotificacaoUrgente("Infra", email, 2);
            notif.enviar("Servidor fora do ar");

            email.getMensagensEnviadas().forEach(msg ->
                    assertTrue(msg.contains("URGENTE"),
                            "Todas as mensagens urgentes devem conter 'URGENTE'"));
        }

        @Test
        @DisplayName("getInfo() deve conter título, repetições e canal")
        void getInfoDeveConterDadosCorretos() {
            var notif = new NotificacaoUrgente("Monitor", sms, 5);
            String info = notif.getInfo();

            assertTrue(info.contains("Monitor"));
            assertTrue(info.contains("5"));
            assertTrue(info.contains("SMS"));
        }

        @Test
        @DisplayName("deve lançar exceção quando repetições for menor que 1")
        void deveLancarExcecaoParaRepeticoesInvalidas() {
            assertThrows(IllegalArgumentException.class,
                    () -> new NotificacaoUrgente("X", sms, 0));
        }
    }

    @Nested
    @DisplayName("Princípio do Bridge — independência entre abstração e implementação")
    class BridgePrincipioTest {

        @Test
        @DisplayName("mesma abstração deve funcionar com canais diferentes sem alterar o código")
        void mesmaAbstracaoFuncionaComCanaisDiferentes() {
            var porEmail = new NotificacaoSimples("Lembrete", email, 1);
            var porSMS   = new NotificacaoSimples("Lembrete", sms,   1);

            porEmail.enviar("Reunião em 10 minutos");
            porSMS.enviar("Reunião em 10 minutos");

            assertEquals(1, email.getMensagensEnviadas().size());
            assertEquals(1, sms.getMensagensEnviadas().size());
        }

        @Test
        @DisplayName("trocar o canal em runtime não afeta a abstração")
        void mesmoMensagemPorDoisCanais() {
            var canais = new NotificadorCanal[]{email, sms};
            for (var canal : canais) {
                new NotificacaoUrgente("Alerta", canal, 1).enviar("Disco cheio");
            }

            assertEquals(1, email.getMensagensEnviadas().size());
            assertEquals(1, sms.getMensagensEnviadas().size());
        }

        @Test
        @DisplayName("diferentes abstrações devem poder compartilhar o mesmo canal")
        void diferentesAbstracoesCompartilhamMesmoCanal() {
            var simples = new NotificacaoSimples("Info",    email, 1);
            var urgente = new NotificacaoUrgente("Critico", email, 2);

            simples.enviar("Backup concluído");
            urgente.enviar("Falha no banco de dados");

            assertEquals(3, email.getMensagensEnviadas().size());
        }
    }
}