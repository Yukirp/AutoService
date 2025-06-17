package br.com.autoservice;

import javax.swing.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.io.File;
import java.util.List;

public class ExcluirCarroFrame extends JFrame {

    private JTextField placaField;

    public ExcluirCarroFrame() {
        setTitle("Excluir Carro");
        setSize(350, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel placaLabel = new JLabel("Placa do carro:");
        placaLabel.setBounds(20, 20, 120, 25);
        placaField = new JTextField();
        placaField.setBounds(140, 20, 150, 25);

        JButton excluirBtn = new JButton("Excluir");
        excluirBtn.setBounds(110, 60, 120, 30);

        add(placaLabel);
        add(placaField);
        add(excluirBtn);

        excluirBtn.addActionListener(e -> excluirCarro());

        setVisible(true);
    }

    private void excluirCarro() {
        String placa = placaField.getText().trim();

        if (placa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe a placa do carro.");
            return;
        }

        Carro carro = CarroService.buscarCarroPorPlaca(placa);
        if (carro == null) {
            JOptionPane.showMessageDialog(this, "Carro não encontrado.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente excluir o carro:\n" +
            "Placa: " + carro.getPlaca() + "\nModelo: " + carro.getModelo() + "\nCliente: " + carro.getCliente(),
            "Confirmar exclusão",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            JPasswordField passwordField = new JPasswordField();
            int result = JOptionPane.showConfirmDialog(
                this,
                passwordField,
                "Digite a senha de administrador:",
                JOptionPane.OK_CANCEL_OPTION
            );

            if (result == JOptionPane.OK_OPTION) {
                String senhaDigitada = new String(passwordField.getPassword());
                String senhaDigitadaCriptografada = SegurancaUtil.criptografar(senhaDigitada);
                String senhaAdmCriptografada = "2f2ff0ec875a9bfa234009ac5f3f2109"; 

                if (senhaDigitadaCriptografada.equals(senhaAdmCriptografada)) {
                    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                        session.beginTransaction();
                        session.clear();
                        carro = session.get(Carro.class, carro.getId());

                        if (carro == null) {
                            JOptionPane.showMessageDialog(this, "Carro não encontrado na base de dados.");
                            return;
                        }

                        Query<Servico> query = session.createQuery(
                            "FROM Servico WHERE carro.id = :carroId", Servico.class
                        );
                        query.setParameter("carroId", carro.getId());
                        List<Servico> servicos = query.list();

                        File pdf = RelatorioPDF.gerarRelatorioServicos(carro, servicos);
                        System.out.println("Relatório salvo em: " + pdf.getAbsolutePath());

                        for (Servico s : servicos) {
                            session.delete(s);
                        }

                        session.delete(carro);
                        session.getTransaction().commit();

                        JOptionPane.showMessageDialog(this, "Carro e serviços excluídos com sucesso.");
                        dispose();
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Erro ao excluir o carro e serviços.");
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "Senha incorreta.");
                }
            }
        }
    }
}
