import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Classe utilitária para validar e capturar entradas do utilizador.
 * Inclui métodos para ler números, datas e NIFs com validação.
 *
 * @author Cíntia Cumbane (2020244607)
 * Cristiana Gonçalves (2019239753)
 * @version 3.0
 */
public class InputUtils {
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Lê e retorna um número do tipo double, com validação de entrada.
     *
     * @param mensagem Mensagem para solicitar a entrada.
     * @return Um número do tipo double inserido pelo utilizador.
     */
    public static double lerDouble(String mensagem) {
        double numero = 0;
        boolean entradaValida = false;

        while (!entradaValida) {
            System.out.print(mensagem);
            try {
                numero = scanner.nextDouble();
                scanner.nextLine();
                entradaValida = true;
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida! Por favor, insira um número válido.");
                scanner.nextLine();
                if (!desejaTentarNovamente()) {
                    throw new RuntimeException("Operação cancelada pelo utilizador.");
                }
            }
        }
        return numero;
    }

    /**
     * Lê e retorna um número inteiro, com validação de entrada.
     *
     * @param mensagem Mensagem para solicitar a entrada.
     * @return Um número inteiro inserido pelo utilizador.
     */
    public static int lerInt(String mensagem) {
        int numero = 0;
        boolean entradaValida = false;

        while (!entradaValida) {
            System.out.print(mensagem);
            try {
                numero = scanner.nextInt();
                scanner.nextLine(); // Consumir a quebra de linha
                entradaValida = true;
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida! Por favor, insira um número inteiro válido.");
                scanner.nextLine(); // Limpar o buffer de entrada
                if (!desejaTentarNovamente()) {
                    throw new RuntimeException("Operação cancelada pelo utilizador.");
                }
            }
        }
        return numero;
    }

    /**
     * Lê e retorna uma data válida no formato "yyyy-MM-dd".
     *
     * @param mensagem Mensagem para solicitar a entrada.
     * @return Uma instância de LocalDate representando a data inserida.
     */
    public static LocalDate lerData(String mensagem) {
        LocalDate data = null;
        boolean entradaValida = false;

        while (!entradaValida) {
            System.out.print(mensagem);
            try {
                String entrada = scanner.nextLine();
                data = LocalDate.parse(entrada);
                entradaValida = true;
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida! Por favor, insira uma data no formato yyyy-MM-dd.");
                if (!desejaTentarNovamente()) {
                    throw new RuntimeException("Operação cancelada pelo utilizador.");
                }
            }
        }
        return data;
    }

    /**
     * Lê e retorna um NIF válido com exatamente 4 dígitos.
     *
     * @param mensagem Mensagem para solicitar a entrada.
     * @return Uma string representando um NIF válido de 4 dígitos.
     */
    public static String lerNifValido(String mensagem) {
        String nif = null;
        boolean entradaValida = false;

        while (!entradaValida) {
            System.out.print(mensagem);
            nif = scanner.nextLine().trim();

            // Validar se tem exatamente 4 dígitos
            if (!nif.matches("\\d{4}")) {
                System.out.println("NIF inválido! O NIF deve conter exatamente 4 dígitos.");
                if (!desejaTentarNovamente()) {
                    throw new RuntimeException("Operação cancelada pelo utilizador.");
                }
            } else {
                entradaValida = true;
            }
        }
        return nif;
    }

    /**
     * Pergunta ao utilizador se deseja tentar novamente.
     *
     * @return true se o utilizador responder "s", false caso contrário.
     */
    private static boolean desejaTentarNovamente() {
        System.out.print("Deseja tentar novamente? (s/n): ");
        String resposta = scanner.nextLine().trim().toLowerCase();
        return resposta.equals("s");
    }
}
