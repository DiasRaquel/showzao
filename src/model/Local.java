package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import dao.Conexao;

public class Local {

    public int id;
    public String nome;

    // Construtor vazio
    public Local() {}

    // Construtor com todos os argumentos
    public Local(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    // Método para cadastrar um local e retornar o ID gerado
    private static int cadastrar(String nome) {
        String sql = "INSERT INTO local (nome) VALUES (?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nome);
            ps.executeUpdate();

            // Obtém o ID gerado automaticamente
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Retorna o ID do local cadastrado
            }
            JOptionPane.showMessageDialog(null, "Local cadastrado com sucesso!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar local: " + e.getMessage());
        }
        return -1; // Retorna -1 se ocorrer algum erro
    }

    // Método para obter a lista de locais
    public static List<Local> getLocais() {
        List<Local> lista = new ArrayList<>();
        String sql = "SELECT id, nome FROM local ORDER BY nome";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Local local = new Local();
                local.id = rs.getInt("id");
                local.nome = rs.getString("nome");
                lista.add(local);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao obter locais: " + e.getMessage());
        }
        return lista;
    }

    public static String verificarOuCadastrar() {
        List<Local> locais = getLocais();
    
        if (locais.isEmpty()) {
            int opcaoCadastro = JOptionPane.showConfirmDialog(null,
                    "Não há locais cadastrados. Deseja cadastrar um novo local?",
                    "Cadastro de Local", JOptionPane.YES_NO_OPTION);
    
            if (opcaoCadastro == JOptionPane.YES_OPTION) {
                String nomeLocal = JOptionPane.showInputDialog("Digite o nome do novo local:");
                if (nomeLocal != null && !nomeLocal.isEmpty()) {
                    int idLocal = cadastrar(nomeLocal); // Cadastra o novo local e retorna o ID
                    return String.valueOf(idLocal); // Retorna o ID do local cadastrado como String
                } else {
                    JOptionPane.showMessageDialog(null, "Nome do local não pode ser vazio.");
                    return null; // Retorna null se o nome do local for vazio
                }
            } else {
                JOptionPane.showMessageDialog(null, "Operação cancelada.");
                return null; // Retorna null se o usuário não deseja cadastrar um novo local
            }
        } else {
            // Usuário escolhe o local existente
            Local escolhido = escolherLocal(locais);
            if (escolhido != null) {
                return String.valueOf(escolhido.id); // Retorna o ID do local escolhido como String
            } else {
                return null; // Retorna null se o usuário cancelar a escolha
            }
        }
    }

    // Método para escolher um local existente
    private static Local escolherLocal(List<Local> locais) {
        if (locais.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Não há locais cadastrados.");
            return null; // Retorna null se não houver locais cadastrados
        }

        String[] opcoes = locais.stream().map(l -> l.nome).toArray(String[]::new);

        String escolha = (String) JOptionPane.showInputDialog(null,
                "Escolha um local:",
                "Escolha de Local",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[0]);

        // Encontra o local escolhido
        for (Local local : locais) {
            if (local.nome.equalsIgnoreCase(escolha)) {
                return local; // Retorna o local escolhido
            }
        }

        return null; // Retorna null se o usuário cancelar a escolha
    }

    // Método para obter a lista de nomes de locais
    public static List<String> obterNomesLocais() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT nome FROM local ORDER BY nome";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(rs.getString("nome"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao obter locais: " + e.getMessage());
        }
        return lista;
    }
}
