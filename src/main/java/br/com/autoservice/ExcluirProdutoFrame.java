package br.com.autoservice;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class ExcluirProdutoFrame extends JFrame {

    private JTextField nomeField;
    private JTextField marcaField;
    private JTextField quantidadeField;

    public ExcluirProdutoFrame() {
        setTitle("Excluir Produto");
        setSize(400, 210);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel nomeLabel = new JLabel("Nome do Produto:");
        nomeLabel.setBounds(20, 20, 120, 25);
        nomeField = new JTextField();
        nomeField.setBounds(150, 20, 210, 25);

        JLabel marcaLabel = new JLabel("Marca do Produto:");
        marcaLabel.setBounds(20, 60, 120, 25);
        marcaField = new JTextField();
        marcaField.setBounds(150, 60, 210, 25);

        JLabel quantidadeLabel = new JLabel("Quantidade:");
        quantidadeLabel.setBounds(20, 100, 120, 25);
        quantidadeField = new JTextField();
        quantidadeField.setBounds(150, 100, 210, 25);

        JButton excluirBtn = new JButton("Excluir");
        excluirBtn.setBounds(140, 140, 120, 30);

        add(nomeLabel);
        add(nomeField);
        add(marcaLabel);
        add(marcaField);
        add(quantidadeLabel);
        add(quantidadeField);
        add(excluirBtn);

        excluirBtn.addActionListener((ActionEvent e) -> excluirProduto());

        setVisible(true);
    }

    private void excluirProduto() {
        String nomeProduto = nomeField.getText().trim();
        String marcaProduto = marcaField.getText().trim();
        String quantidadeStr = quantidadeField.getText().trim();

        if (nomeProduto.isEmpty() || marcaProduto.isEmpty() || quantidadeStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
            return;
        }

        int quantidade;
        try {
            quantidade = Integer.parseInt(quantidadeStr);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida.");
            return;
        }
        if (quantidade <= 0) {
            JOptionPane.showMessageDialog(this, "Informe uma quantidade maior que zero.");
            return;
        }

        // 1) exige usuário logado
        Usuario usuarioLogado = UsuarioLogado.getUsuario();
        if (usuarioLogado == null) {
            JOptionPane.showMessageDialog(this, "Nenhum usuário logado. Faça login novamente.");
            return;
        }

        // (opcional) exige perfil ADMIN
        try {
            java.lang.reflect.Method m = usuarioLogado.getClass().getMethod("getPerfil");
            Object perfil = m.invoke(usuarioLogado);
            if (perfil == null || !"ADMIN".equalsIgnoreCase(perfil.toString())) {
                JOptionPane.showMessageDialog(this, "Apenas administradores podem excluir produtos.");
                return;
            }
        } catch (NoSuchMethodException ignore) {
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // 2) confirma senha do usuário logado (SHA-256)
        JPasswordField pwd = new JPasswordField();
        JPanel p = new JPanel(new java.awt.GridLayout(2, 1, 6, 6));
        p.add(new JLabel("Confirmar como: " + usuarioLogado.getLogin()));
        p.add(pwd);
        int r = JOptionPane.showConfirmDialog(this, p, "Digite sua senha (administrador):",
                                              JOptionPane.OK_CANCEL_OPTION);
        if (r != JOptionPane.OK_OPTION) return;

        char[] senhaArr = pwd.getPassword();
        String senhaDigitada = new String(senhaArr);
        java.util.Arrays.fill(senhaArr, '\0');

        Usuario usuarioBanco = buscarUsuarioPorLogin(usuarioLogado.getLogin());
        if (usuarioBanco == null || usuarioBanco.getSenha() == null || usuarioBanco.getSenha().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não foi possível validar suas credenciais.");
            return;
        }
        String hashBanco = usuarioBanco.getSenha();

        // DEBUG opcional
        String shaCalc = SegurancaUtil.sha256Hex(senhaDigitada);
        System.out.println("Auth(prod) -> login=" + usuarioBanco.getLogin()
                + ", hashLen=" + (hashBanco == null ? 0 : hashBanco.length())
                + ", hashPrefix=" + (hashBanco == null ? "<null>" :
                    (hashBanco.length() >= 7 ? hashBanco.substring(0, 7) : hashBanco)));
        System.out.println("Auth(prod) calc -> shaCalc=" + shaCalc);

        if (!SegurancaUtil.verificarSenha(senhaDigitada, hashBanco)) {
            JOptionPane.showMessageDialog(this, "Senha incorreta. Exclusão não permitida.");
            return;
        }

        // 3) localizar o produto
        List<Produto> candidatos = ProdutoService.buscarProdutoPorNome(nomeProduto);
        if (candidatos == null || candidatos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Produto não encontrado.");
            return;
        }

        // se houver vários com mesmo nome, tenta casar por marca
        Produto produto = null;
        for (Produto pdt : candidatos) {
            if (pdt.getMarca() != null && pdt.getMarca().equalsIgnoreCase(marcaProduto)) {
                produto = pdt;
                break;
            }
        }
        if (produto == null) {
            // fallback: usa o primeiro da lista
            produto = candidatos.get(0);
        }

        if (produto.getQuantidade() < quantidade) {
            JOptionPane.showMessageDialog(this, "Quantidade a excluir é maior que a disponível.");
            return;
        }

        int novaQtd = produto.getQuantidade() - quantidade;

        // 4) aplicar alteração
        try {
            if (novaQtd > 0) {
                // diminui somente
                ProdutoService.diminuirQuantidade(produto, quantidade);
            } else {
                // zera → remover produto
                ProdutoService.diminuirQuantidade(produto, quantidade); // caso sua service já trate <= 0, pode remover esta linha
                ProdutoService.excluirProduto(produto);
            }

            JOptionPane.showMessageDialog(this, "Operação concluída com sucesso.");
            nomeField.setText("");
            marcaField.setText("");
            quantidadeField.setText("");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Falha ao excluir/diminuir quantidade do produto.");
        }
    }

    /** Recarrega usuário por login para validar senha. */
    private Usuario buscarUsuarioPorLogin(String login) {
        if (login == null || login.isEmpty()) return null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Usuario> q = session.createQuery(
                "FROM Usuario u WHERE u.login = :login", Usuario.class);
            q.setParameter("login", login);
            return q.uniqueResult();
        }
    }
}
