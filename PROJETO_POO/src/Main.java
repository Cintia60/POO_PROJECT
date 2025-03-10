import java.util.*;
import java.io.*;

/**
 * Classe principal que inicializa o sistema de faturação.
 * Permite ao utilizador interagir com o sistema através de um menu.
 *
 * @author Cíntia Cumbane (2020244607)
 * Cristiana Gonçalves (2019239753)
 * @version 3.0
 */
public class Main {
    public static void main(String[] args) {
        POOFS sistema = new POOFS();
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            System.out.println("\nMenu:");
            System.out.println("1. Criar Cliente");
            System.out.println("2. Editar Cliente");
            System.out.println("3. Listar Clientes");
            System.out.println("4. Criar Fatura");
            System.out.println("5. Editar Fatura");
            System.out.println("6. Listar Faturas");
            System.out.println("7. Visualizar Fatura");
            System.out.println("8. Importar Faturas");
            System.out.println("9. Exportar Faturas");
            System.out.println("10. Exibir Estatísticas");
            System.out.println("11. Sair");
            System.out.print("Escolha uma opção: ");

            try {
                int opcao = scanner.nextInt();
                scanner.nextLine();

                // Processamento da opção selecionada
                switch (opcao) {
                    case 1 -> sistema.criarCliente();
                    case 2 -> sistema.editarCliente();
                    case 3 -> sistema.listarClientes();
                    case 4 -> {
                        try {
                            sistema.criarFatura();
                        } catch (RuntimeException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    case 5 -> sistema.editarFatura();
                    case 6 -> sistema.listarFaturas();
                    case 7 -> sistema.visualizarFatura();
                    case 8 -> sistema.importarFaturas();
                    case 9 -> sistema.exportarFaturas();
                    case 10 -> sistema.exibirEstatisticas();
                    case 11 -> {
                        System.out.println("Saindo...");
                        sistema.getFicheiroObjetoHandler().salvarDados();
                        continuar = false;
                    }
                    default -> System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (InputMismatchException e) {
                // Tratamento de erro caso a entrada não seja um número válido
                System.out.println("Entrada inválida! Por favor, insira um número entre 1 e 11.");
                scanner.nextLine();
            }


            if (continuar) {
                System.out.print("Deseja inserir outra opção? (s/n): ");
                String resposta = scanner.nextLine().trim().toLowerCase();
                if (!resposta.equals("s")) {
                    continuar = false;
                    System.out.println("Encerrando o programa.");
                }
            }
        }

        scanner.close();
    }
}
