package br.com.autoservice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File; // Importa a classe File

public class PrincipalFrame extends JFrame {

    public PrincipalFrame() {
        setTitle("AutoService - Sistema de Gestão de Veículos");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JButton cadastrarProdutoBtn = new JButton("Cadastrar Produto");
        JButton btnCadastrarCarro = new JButton("🚗 Cadastrar Carro");
        JButton btnCadastrarServico = new JButton("🛠️ Cadastrar Serviço");
        JButton btnListarCarros = new JButton("📋 Listar Carros");
        JButton btnListarServicos = new JButton("🧾 Listar Serviços");
        JButton btnBuscar = new JButton("🔍 Buscar Carro");
        JButton btnExcluirCarro = new JButton("❌ Excluir Carro");
        JButton btnImportarProdutos = new JButton("📄 Importar Produtos");
        JButton btnListarProdutos = new JButton("📦 Listar Produtos");
        JButton btnSair = new JButton("⛔ Sair");
        JButton btnExcluirProduto = new JButton("❌ Excluir Produto");
        JButton btnBuscarProduto = new JButton("🔍 Buscar Produto");
        JButton venderProdutoBtn = new JButton("Vender Produto");
        JButton btnRelatorios = new JButton("📑 Relatórios");
            
        // Título no topo
        JLabel titulo = new JLabel("🔧 AutoService", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(titulo, BorderLayout.NORTH);
        add(cadastrarProdutoBtn);
        
        // Painel dos botões
        JPanel painel = new JPanel(new GridLayout(9, 1, 10, 10));
        painel.setBorder(BorderFactory.createTitledBorder("Menu Principal"));

        
        
        painel.add(btnCadastrarCarro);
        painel.add(cadastrarProdutoBtn);
        painel.add(btnCadastrarServico);
        painel.add(btnImportarProdutos);
        painel.add(btnListarCarros);
        painel.add(btnListarProdutos);
        painel.add(btnBuscar);
        painel.add(btnBuscarProduto);
        painel.add(btnExcluirCarro);
        painel.add(btnExcluirProduto);
        painel.add(btnListarServicos);
        painel.add(venderProdutoBtn);
        painel.add(btnRelatorios);
        painel.add(btnSair);
        
        add(painel, BorderLayout.CENTER);

        // Ações dos botões
        btnCadastrarCarro.addActionListener(e -> new CadastroCarroFrame());
        btnCadastrarServico.addActionListener(e -> new CadastroServicoFrame());
        btnListarCarros.addActionListener(e -> new ListarCarrosFrame());
        btnListarServicos.addActionListener(e -> new ListarServicosFrame());
        btnBuscar.addActionListener(e -> new BuscarCarroFrame());
        btnBuscar.addActionListener(e -> new BuscarCarroFrame()); // sua nova tela com filtro
        btnExcluirCarro.addActionListener(e -> new ExcluirCarroFrame());
        btnListarProdutos.addActionListener(e -> new ListarProdutosFrame());
        btnSair.addActionListener(e -> System.exit(0));
        btnExcluirProduto.addActionListener(e -> new ExcluirProdutoFrame());
        btnBuscarProduto.addActionListener(e -> new BuscarProdutoFrame());
        btnRelatorios.addActionListener(e -> new RelatorioFrame());
        cadastrarProdutoBtn.addActionListener((ActionEvent e) -> {
            new CadastrarProdutoFrame(); // Abre a tela de cadastro de produto
        });
        
         btnImportarProdutos.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Selecione o arquivo PDF");
            int result = fileChooser.showOpenDialog(null);
            
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                System.out.println("Arquivo selecionado: " + selectedFile.getAbsolutePath());
                // Aqui você pode chamar a lógica para importar os produtos do PDF
            }
        });
         
          venderProdutoBtn.addActionListener((ActionEvent e) -> {
            new VenderProdutoFrame(); // Abre a tela de venda de produto
        });
        
        setVisible(true);
    }
}
