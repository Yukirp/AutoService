package br.com.autoservice;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ExcluirProdutoFrame extends JFrame {

    private JTextField nomeField;
    private JTextField marcaField;
    private JTextField quantidadeField;
    private JPasswordField senhaField;

    public ExcluirProdutoFrame() {
        setTitle("Excluir Produto");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel nomeLabel = new JLabel("Nome do Produto:");
        nomeLabel.setBounds(20, 20, 120, 25);
        nomeField = new JTextField();
        nomeField.setBounds(140, 20, 200, 25);

        JLabel marcaLabel = new JLabel("Marca do Produto:");
        marcaLabel.setBounds(20, 60, 120, 25);
        marcaField = new JTextField();
        marcaField.setBounds(140, 60, 200, 25);

        JLabel quantidadeLabel = new JLabel("Quantidade:");
        quantidadeLabel.setBounds(20, 100, 120, 25);
        quantidadeField = new JTextField();
        quantidadeField.setBounds(140, 100, 200, 25);

        JLabel senhaLabel = new JLabel("Senha de Confirmação:");
        senhaLabel.setBounds(20, 140, 160, 25);
        senhaField = new JPasswordField();
        senhaField.setBounds(180, 140, 160, 25);

        JButton excluirBtn = new JButton("Excluir");
        excluirBtn.setBounds(140, 180, 120, 30);

        add(nomeLabel);
        add(nomeField);
        add(marcaLabel);
        add(marcaField);
        add(quantidadeLabel);
        add(quantidadeField);
        add(senhaLabel);
        add(senhaField);
        add(excluirBtn);

        excluirBtn.addActionListener((ActionEvent e) -> excluirProduto());

        setVisible(true);
    }

    private void excluirProduto() {
        String nomeProduto = nomeField.getText().trim();
        String marcaProduto = marcaField.getText().trim();
        String quantidadeStr = quantidadeField.getText().trim();
        char[] senhaArray = senhaField.getPassword();

        if (nomeProduto.isEmpty() || marcaProduto.isEmpty() || quantidadeStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
            return;
        }

        int quantidade = Integer.parseInt(quantidadeStr);

        if (senhaArray.length == 0) {
            JOptionPane.showMessageDialog(this, "Informe a senha de confirmação.");
            return;
        }

        String senhaDigitada = new String(senhaArray);
        String senhaDigitadaCriptografada = SegurancaUtil.criptografar(senhaDigitada);
        String senhaAdmCriptografada = "2f2ff0ec875a9bfa234009ac5f3f2109"; 

        if (!senhaDigitadaCriptografada.equals(senhaAdmCriptografada)) {
            JOptionPane.showMessageDialog(this, "Senha incorreta. Exclusão não permitida.");
            return;
        }

        List<Produto> produtos = ProdutoService.buscarProdutoPorNome(nomeProduto);
        if (produtos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Produto não encontrado.");
            return;
        }

        Produto produto = produtos.get(0);

        if (produto.getQuantidade() < quantidade) {
            JOptionPane.showMessageDialog(this, "Quantidade a excluir é maior que a disponível.");
            return;
        }

        ProdutoService.diminuirQuantidade(produto, quantidade);
        ProdutoService.excluirProduto(produto);

        JOptionPane.showMessageDialog(this, "Produto excluído com sucesso!");
        nomeField.setText("");
        marcaField.setText("");
        quantidadeField.setText("");
        senhaField.setText("");
    }
}
