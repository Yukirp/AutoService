import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class ImportarProdutosDoPDF {

    // Método que irá abrir o arquivo PDF e extrair os produtos
    public void importarProdutosDoPDF() {
        // Abrindo o arquivo PDF
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecione o arquivo PDF");
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try {
                PDDocument document = PDDocument.load(file);

                // Usando PDFTextStripper para extrair o texto do PDF
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);

                // Processar o texto extraído
                System.out.println("Conteúdo extraído: ");
                System.out.println(text);

                // Dividindo o texto em linhas e extraindo os dados dos produtos
                String[] linhas = text.split("\n");
                for (String linha : linhas) {
                    if (linha.startsWith("Produto:")) {
                        String[] produtoDados = linha.split(",");
                        String nomeProduto = produtoDados[0].substring(9).trim();  // Remove "Produto:" e espaços
                        String precoProduto = produtoDados[1].substring(6).trim(); // Exemplo de preço

                        // Exibe os produtos importados
                        System.out.println("Produto: " + nomeProduto + ", Preço: " + precoProduto);

                        // Aqui você pode adicionar uma lógica para salvar o produto no banco de dados ou em uma lista
                    }
                }

                document.close();
                JOptionPane.showMessageDialog(null, "Produtos importados com sucesso!");

            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao importar o PDF.");
            }
        }
    }

    public static void main(String[] args) {
        // Crie uma instância da classe ImportarProdutosDoPDF e chame o método para testar
        ImportarProdutosDoPDF importar = new ImportarProdutosDoPDF();
        importar.importarProdutosDoPDF();
    }
}
