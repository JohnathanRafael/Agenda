import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;


abstract class ItemParaArmazenar implements Serializable {
	public abstract boolean igual (ItemParaArmazenar ipa);
}

class PessoaAgenda extends ItemParaArmazenar{
    protected int codigo;
    protected String nome,
                        endereco,
                        telefone,
                        anotacoes;

    public boolean igual(ItemParaArmazenar  pa){
        if (codigo == ((PessoaAgenda) pa).getCodigo()) {
                   return true;
               }
        return false;
    }

    PessoaAgenda(String nome){ //solução para o problema de shadowing (sombreamento)
        this.nome = nome;
    }

    PessoaAgenda(String nome, String endereco, 
                    String telefone, String anotacoes){
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
        this.anotacoes = anotacoes;
    }

    public void setCodigo(int codigo){
        this.codigo = codigo;
    }
    public void setNome(String nome){
        this.nome = nome;
    }
    public void setEndereco(String endereco){
        this.endereco = endereco;
    }
    public void setTelefone(String telefone){
        this.telefone = telefone;
    }
    public void setAnotacoes(String anotacoes){
        this.anotacoes = anotacoes;
    }
    
    public int getCodigo(){
        return codigo;
    }
    public String getNome(){
        return nome;
    }
    public String getEndereco(){
        return endereco;
    }
    public String getTelefone(){
        return telefone;
    }
    public String getAnotacoes(){
        return anotacoes;
    }
}

class Armazenamento {
    private static Armazenamento instance;
    protected int scan;
	String arquivo;
	ArrayList<ItemParaArmazenar> arl;
	
	private Armazenamento (String arq){
		arquivo = arq;
		arl = new ArrayList<ItemParaArmazenar>();
		montarLista(arl);
        lerId();
	}

     public static Armazenamento getInstance() {
        if(Armazenamento.instance == null ) {
            Armazenamento.instance = new Armazenamento("Pessoas.dat");
        }return Armazenamento.instance; 
    }

    protected int lerId() {
        try {
            DataInputStream ler = new DataInputStream(new FileInputStream("controlaID.dat"));
            scan = ler.readInt();
            ler.close();
        }
		catch (FileNotFoundException e) {}
		catch (IOException e) {}
        return scan;
    }

	protected int armazenarId(int qdId) {
        try {
            
            if(quantidadeRegistros() == 0)
                qdId = 0;
            else
                qdId++;
            DataOutputStream teste = new DataOutputStream(new FileOutputStream("controlaID.dat"));
            teste.writeInt(qdId);
            teste.close();
        }
		catch (FileNotFoundException e) {}
		catch (IOException e) {}
        return qdId;
    }

	protected void armazenar(ArrayList<ItemParaArmazenar> arl) {
		try {
			File f = new File(arquivo);
			f.delete();
			f = null;
			
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(arquivo));
			for (int i=0; i < arl.size() ; i++ ) {
				out.writeObject((ItemParaArmazenar) arl.get(i));	
			}
			
			out.close();
		}
		catch (FileNotFoundException e) {}
		catch (IOException e) {}
	}
	
	protected void montarLista(ArrayList<ItemParaArmazenar> arl) {
		arl.clear();
		try {
			ObjectInputStream in = new ObjectInputStream (new FileInputStream(arquivo));
			ItemParaArmazenar it;
			while ((it = (ItemParaArmazenar) in.readObject()) != null){
				arl.add(it);
			}
			in.close();
		}
		catch (IOException e) {}
		catch (ClassNotFoundException e) {}		
	}
	
	public void inserir(ItemParaArmazenar ipa) {
        
            PessoaAgenda pa = (PessoaAgenda) ipa;
            pa.setCodigo(armazenarId(lerId()));
			arl.add(ipa);
			armazenar(arl);
	}

	public ItemParaArmazenar excluir(int pos){
		ItemParaArmazenar it=null;
		if((pos >= 0) && (pos < quantidadeRegistros()) ){
			it = arl.remove(pos);
			armazenar(arl);
		}		
		return it;
	}

	public int alterar(ItemParaArmazenar ipa){
		ItemParaArmazenar item;
		for (int i=0; i < arl.size(); i++){
			item = arl.get(i);
			if(item.igual(ipa)){
				System.out.println("encontrou");
				arl.set(i, ipa);
				armazenar(arl);
				return i;
			}
		}

		return -1; // indica que não houve alteração.

	}
	public ItemParaArmazenar obter(int pos) {
		
        int X=0;
		ItemParaArmazenar it=null;
		if((pos >= 0) && (pos < quantidadeRegistros()) ){
			it = arl.get(pos);
            X++;
		}		
        
		return it;
	}	
	
	public int quantidadeRegistros(){
		return arl.size();
	}
	
	public void limparArquivo(){
		File f = new File(arquivo);
		f.delete();
		f = null;
		arl.clear();
	}

    public int encontra(int identificador)
    {
         for(int i=0; i < quantidadeRegistros();i++)
            {
                PessoaAgenda teste = (PessoaAgenda) obter(i);
                if(teste.getCodigo() == identificador)
                {
                    return 1;
                }
            }
        return 0; // Não achou ninguém;
    }

    public int encontra(String pessoa)
    {
         for(int i=0; i < quantidadeRegistros();i++)
            {
                PessoaAgenda teste = (PessoaAgenda) obter(i);
                if(teste.getNome().equals(pessoa))
                {
                    return 1;
                }
            }
        return 0; // Não achou ninguém;
    }
}

class PainelFrontal extends JPanel{

    private static PainelFrontal instance;
    //dados
    JTextField nome = new JTextField(10);
    JTextField telefone = new JTextField(10);
    JTextField endereco = new JTextField(20);

    //anotacoes
    JTextArea caixa = new JTextArea(10,20);

    // destinado a opcao de consultar 
    JTextField consultarId = new JTextField(10);
    JTextField consultarNome = new JTextField(10);
    

    //tabela
    DefaultTableModel criarTabela = new DefaultTableModel(){
        public boolean isCellEditable(int rowIndex, int mColIndex){ //deixa a tabela nao editavel 
         return false; 
    }
    };
    JTable tabela = new JTable(criarTabela);

    public PainelFrontal(){

        //adiciona o conteudo no painel
        setLayout(new BorderLayout()); 
        add(picture(), BorderLayout.NORTH);
        add(painel(), BorderLayout.WEST);
        add(table(), BorderLayout.EAST);       
    }

    public static PainelFrontal getInstance() {
        if(PainelFrontal.instance == null ) {
            PainelFrontal.instance = new PainelFrontal();
        }return PainelFrontal.instance;
    }

    public JPanel picture(){
        //configuracao do titulo - imagem
        JPanel pic = new JPanel();
        ImageIcon imagem = new ImageIcon("agenda.png");
        imagem.setImage(imagem.getImage().getScaledInstance(400, 200, 1));// tamanho da imagem fazer uma 1000x200 
        JLabel agenda = new JLabel(imagem);
        
        pic.add(agenda);
        return pic;
    }

    public JPanel table(){

        // criando a tabela 
        JPanel tabe = new JPanel(new BorderLayout(20, 20));
		tabe.setPreferredSize(new Dimension(500, 500));

        Box navigation = new Box(BoxLayout.X_AXIS);
        navigation.add(Box.createRigidArea(new Dimension(90,0)));

        JButton anterior = new JButton("Anterior");
        JButton proximo = new JButton("Proximo");
        JButton fim = new JButton("Fim de navegação");

        proximo.addActionListener(new Proximo());
        anterior.addActionListener(new Anterior());
        
        navigation.add(anterior);
        navigation.add(Box.createRigidArea(new Dimension(10,10)));
        navigation.add(proximo);
        navigation.add(Box.createRigidArea(new Dimension(10,10)));
        navigation.add(fim);

        JScrollPane pane = new JScrollPane(tabela);

        tabela.getTableHeader().setResizingAllowed(false);
        tabela.getTableHeader().setReorderingAllowed(false);
        
        Object [] titulo = null;
        titulo = new Object [] {"ID", "NOME", "ENDEREÇO", "TELEFONE"};
        Object [][] dados = new Object[0][0];  

        criarTabela.setDataVector(dados, titulo);

        for (int i=0; i < Armazenamento.getInstance().quantidadeRegistros(); i++){
            PessoaAgenda pa = (PessoaAgenda) Armazenamento.getInstance().obter(i);
            Object [] objeto_vetorizado = {pa.getCodigo(), pa.getNome(), pa.getEndereco(), pa.getTelefone()};
            criarTabela.addRow(objeto_vetorizado);
        }  

        tabe.add(navigation, BorderLayout.NORTH);
        tabe.add(pane, BorderLayout.CENTER);
        return tabe;
    }

    

    public JPanel painel(){// configurando textos
        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BorderLayout()); 
        painelPrincipal.add(inserirCaixa(), BorderLayout.EAST);
        painelPrincipal.add(inserirTextos(), BorderLayout.WEST);
        painelPrincipal.add(inserirBotoes(), BorderLayout.SOUTH);

        return painelPrincipal;
    }
    
     public JPanel inserirCaixa(){// configurando text area
        JPanel area = new JPanel(new BorderLayout());
        
        Box central = new Box(BoxLayout.Y_AXIS);

        area.add(new JLabel("Anotações"), BorderLayout.NORTH);
        
        caixa.setLocation(0, 5);
        caixa.setSize(10, 20);
        caixa.setFont(new Font("Times", 0, 14));
	    caixa.setEditable(true);
	    caixa.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(caixa);

        central.add(scroll);

        area.add(central, BorderLayout.CENTER);

        return area;
     }


    public JPanel inserirTextos(){// configurando textos
        JPanel textos = new JPanel();
        Box alinhar = new Box(BoxLayout.Y_AXIS); 

        alinhar.add(new JLabel("Nome: "));
        alinhar.add(nome); // adiciona text area nome
      
        alinhar.add(new JLabel("Telefone: "));
        alinhar.add(telefone); // adiciona text area telefone

        alinhar.add(new JLabel("Endereço: "));
        alinhar.add(endereco); // adiciona text area endereco

        alinhar.add(Box.createRigidArea(new Dimension(0,40)));
        
        alinhar.add(new JLabel("Cosultar por: "));
        alinhar.add(new JLabel("ID "));
        alinhar.add(consultarId);

        alinhar.add(new JLabel("Nome "));
        alinhar.add(consultarNome);

        textos.add(alinhar, BorderLayout.CENTER);
        return textos;
    }

    public JPanel inserirBotoes(){// configurando botoes 
        JPanel botoes = new JPanel();
        Box centralizar = new Box(BoxLayout.X_AXIS);
        centralizar.add(Box.createRigidArea(new Dimension(50,50)));
        
        
        JButton inserir = new JButton("Inserir");
        JButton consultar = new JButton("Consultar");
        JButton alterar = new JButton("Alterar");
        JButton excluir = new JButton("Exluir");
        JButton limpar = new JButton("Limpar");

        limpar.addActionListener(new Limpar());
        inserir.addActionListener(new Inserir());
        excluir.addActionListener(new Excluir());
        alterar.addActionListener(new Alterar());
        consultar.addActionListener(new Consultar());

        centralizar.add(inserir);
        centralizar.add(Box.createRigidArea(new Dimension(10,10)));
        centralizar.add(consultar);
        centralizar.add(Box.createRigidArea(new Dimension(10,10)));
        centralizar.add(alterar);
        centralizar.add(Box.createRigidArea(new Dimension(10,10)));
        centralizar.add(excluir);
        centralizar.add(Box.createRigidArea(new Dimension(10,10)));
        centralizar.add(limpar);

        botoes.add(centralizar);
    
        return botoes;
    }  

    private class Consultar implements ActionListener
    {
        public void actionPerformed(ActionEvent consultar)
        {
            if(consultarNome.getText().isEmpty() && consultarId.getText().isEmpty())
            {
                JOptionPane.showMessageDialog(null, "Informe o valor para consultar! ");
            }
            else if (!consultarNome.getText().isEmpty() && !consultarId.getText().isEmpty())
            {
                JOptionPane.showMessageDialog(null, "Informe um ID ou um nome! Não é aceito os dois campos preenchidos! ");
            }
            else if (!consultarId.getText().isEmpty())
            {
                int verificaId = Armazenamento.getInstance().encontra(Integer.parseInt(consultarId.getText()));

                if (verificaId == 0)
                {
                    JOptionPane.showMessageDialog(null, "ID não encontrado! ");
                }
                else
                {
                    for(int i=0; i < Armazenamento.getInstance().quantidadeRegistros(); i++)
                    {
                        if(Integer.parseInt(consultarId.getText()) == Integer.parseInt(tabela.getValueAt(i, 0).toString()))
                            tabela.changeSelection(i, 0, false, false);
                    }
                    
                    nome.setText(tabela.getValueAt(tabela.getSelectedRow(), 1).toString());
                    telefone.setText(tabela.getValueAt(tabela.getSelectedRow(), 2).toString());
                    endereco.setText(tabela.getValueAt(tabela.getSelectedRow(), 3).toString());

                    PessoaAgenda anota = (PessoaAgenda) Armazenamento.getInstance().obter((tabela.getSelectedRow()));
                    caixa.setText(anota.getAnotacoes());
                }
            }
            else
            {
                ((DefaultTableModel) Consulta.getInstance().result.tabelinha.getModel()).setRowCount(0);
                int verificaNome = Armazenamento.getInstance().encontra(consultarNome.getText());

                if (verificaNome == 0)
                {
                    JOptionPane.showMessageDialog(null, "Nome não encontrado! ");
                }
                else
                {
                    Consulta.getInstance().setVisible(true);
                    for(int i2=0; i2 < Armazenamento.getInstance().quantidadeRegistros(); i2++)
                    {
                        if(consultarNome.getText().equals(tabela.getValueAt(i2, 1).toString()))
                        {
                            PessoaAgenda passa = (PessoaAgenda) Armazenamento.getInstance().obter(i2);
                            Consulta.getInstance().result.insereNaTabela(passa);
                        }
                    }
                    
                }
            }
            
        }
    }

    private class Limpar implements ActionListener
    {
        public void actionPerformed(ActionEvent limpar)
        {
            nome.setText("");
            telefone.setText("");
            endereco.setText("");
            caixa.setText("");
        }
    }

    private class Inserir implements ActionListener
        {
            public void actionPerformed(ActionEvent inserir)
            {
                    PessoaAgenda pa = new PessoaAgenda(nome.getText(), endereco.getText(), telefone.getText(), caixa.getText());
                    Armazenamento.getInstance().inserir(pa);

                    Object [] objeto_vetorizado = {pa.getCodigo(), pa.getNome(), pa.getEndereco(), pa.getTelefone()};
                    criarTabela.addRow(objeto_vetorizado); 

                    nome.setText("");
                    telefone.setText("");
                    endereco.setText("");
                    caixa.setText("");
            }
        }

    private class Excluir implements ActionListener
    {
        public void actionPerformed(ActionEvent excluir)
        {
            
            if(tabela.getSelectedRow() != -1)
            {
                DefaultTableModel apagar = (DefaultTableModel) tabela.getModel();// o excluir se basei na posicao do arrai e nao no id da pessoa 
                Armazenamento.getInstance().excluir((tabela.getSelectedRow()));
                apagar.removeRow((tabela.getSelectedRow()));
            }
            else
                JOptionPane.showMessageDialog(null, "Selecione um contato para excluir! ");
        }
    }

    private class Alterar implements ActionListener
    {
        public void actionPerformed(ActionEvent alterar)
        {
            
            if(tabela.getSelectedRow() != -1)
            { 
                
                PessoaAgenda ap = (PessoaAgenda) Armazenamento.getInstance().obter(tabela.getSelectedRow());
                PessoaAgenda aa = ap;
                
                aa.setAnotacoes(caixa.getText());
                aa.setNome(nome.getText());
                aa.setTelefone(telefone.getText());
                aa.setEndereco(endereco.getText());

                int erro = Armazenamento.getInstance().alterar(aa);

                if(erro == -1) 
                {
                    JOptionPane.showMessageDialog(null, "Não houve alteração");
                }
                else
                {
                    tabela.setValueAt(nome.getText(), tabela.getSelectedRow(), 1);
                    tabela.setValueAt(telefone.getText(), tabela.getSelectedRow(), 2);
                    tabela.setValueAt(endereco.getText(), tabela.getSelectedRow(), 3);
                }   
            }
            else
                JOptionPane.showMessageDialog(null, "Selecione um contato para alterar! ");
        }
    }

    int linha = 0;
    private class Proximo implements ActionListener
    {
        public void actionPerformed(ActionEvent proximo)
        {    
            if(Armazenamento.getInstance().quantidadeRegistros() == 0)
            {   
                JOptionPane.showMessageDialog(null, "Nenhum contato inserido! ");
            }
            else 
            {    
                tabela.clearSelection();
                tabela.changeSelection(linha++, 0, false, false);

                nome.setText(tabela.getValueAt(tabela.getSelectedRow(), 1).toString());
                telefone.setText(tabela.getValueAt(tabela.getSelectedRow(), 2).toString());
                endereco.setText(tabela.getValueAt(tabela.getSelectedRow(), 3).toString());

                PessoaAgenda anota = (PessoaAgenda) Armazenamento.getInstance().obter((tabela.getSelectedRow()));
                caixa.setText(anota.getAnotacoes());
            }   
        }
    }

    private class Anterior implements ActionListener
    {
        public void actionPerformed(ActionEvent anterior)
        {
            if(Armazenamento.getInstance().quantidadeRegistros() == 0)
            {   
                JOptionPane.showMessageDialog(null, "Nenhum contato inserido! ");
            }
            else 
            {
                tabela.clearSelection();
                tabela.changeSelection(--linha, 0, false, false);

                nome.setText(tabela.getValueAt(tabela.getSelectedRow(), 1).toString());
                telefone.setText(tabela.getValueAt(tabela.getSelectedRow(), 2).toString());
                endereco.setText(tabela.getValueAt(tabela.getSelectedRow(), 3).toString());

                PessoaAgenda anota = (PessoaAgenda) Armazenamento.getInstance().obter((tabela.getSelectedRow()));
                caixa.setText(anota.getAnotacoes());
            }
        }
    }
}

class ResultadoConsulta extends JPanel{

    public int selecionado;
    //tabela
    DefaultTableModel table = new DefaultTableModel(){
        public boolean isCellEditable(int rowIndex, int mColIndex){ //deixa a tabela nao editavel 
         return false; 
    }
    };
    JTable tabelinha = new JTable(table);

    public ResultadoConsulta(){
        setLayout(new BorderLayout()); 
        add(table(), BorderLayout.CENTER);
    }

    public void insereNaTabela(PessoaAgenda mt)
    {
        Object [] insere ={mt.getCodigo(), mt.getNome(), mt.getEndereco(), mt.getTelefone()};
        table.addRow(insere); 
    }

    public JPanel table(){


        // criando a tabela 
        JPanel tab = new JPanel(new BorderLayout(20, 20));
        Box colocar = new Box(BoxLayout.X_AXIS);
        colocar.add(Box.createRigidArea(new Dimension(90,0)));

        JButton voltar = new JButton("Voltar");
        JButton selecionar = new JButton("Selecionar");

        selecionar.addActionListener(new Selecionar());
        voltar.addActionListener(new Voltar());


        colocar.add(voltar);
        colocar.add(Box.createRigidArea(new Dimension(10,10)));
        colocar.add(selecionar);
        colocar.add(Box.createRigidArea(new Dimension(10,10)));

		tab.setPreferredSize(new Dimension(500, 500));

        JScrollPane pane = new JScrollPane(tabelinha);

        tabelinha.getTableHeader().setResizingAllowed(false);
        tabelinha.getTableHeader().setReorderingAllowed(false);
        
        Object [] title = null;
        title = new Object [] {"ID", "NOME", "ENDEREÇO", "TELEFONE"};
        Object [][] data = new Object[0][0];  

        table.setDataVector(data, title);

        tab.add(pane, BorderLayout.CENTER);
        tab.add(colocar, BorderLayout.SOUTH);

        return tab;
    }

    private class Selecionar implements ActionListener
    {
        public void actionPerformed(ActionEvent selecionar)
        {
            Consulta.getInstance().setVisible(false);
            selecionado = Integer.parseInt(tabelinha.getValueAt(tabelinha.getSelectedRow(), 0).toString());

            for(int i=0; i < Armazenamento.getInstance().quantidadeRegistros(); i++)
            {
                        
                if(selecionado == Integer.parseInt(PainelFrontal.getInstance().tabela.getValueAt(i, 0).toString()))
                {
                    PainelFrontal.getInstance().tabela.changeSelection(i, 0, false, false);
                    PainelFrontal.getInstance().nome.setText(PainelFrontal.getInstance().tabela.getValueAt(PainelFrontal.getInstance().tabela.getSelectedRow(), 1).toString());
                    PainelFrontal.getInstance().telefone.setText(PainelFrontal.getInstance().tabela.getValueAt(PainelFrontal.getInstance().tabela.getSelectedRow(), 2).toString());
                    PainelFrontal.getInstance().endereco.setText(PainelFrontal.getInstance().tabela.getValueAt(PainelFrontal.getInstance().tabela.getSelectedRow(), 3).toString());

                    PessoaAgenda anota = (PessoaAgenda) Armazenamento.getInstance().obter((PainelFrontal.getInstance().tabela.getSelectedRow()));
                    PainelFrontal.getInstance().caixa.setText(anota.getAnotacoes());
                }            
            }

        }
    }

    private class Voltar implements ActionListener
    {
        public void actionPerformed(ActionEvent voltar)
        {
            Consulta.getInstance().setVisible(false);
        }
    }
}

class Consulta extends JFrame {
    private static Consulta instance;
    static ResultadoConsulta result = new ResultadoConsulta();

    private Consulta(ResultadoConsulta rc){
        // cria o painel principal com o tipo de layout
        // configura o painel para exibir  
        setSize(400, 200);
		setLocation(450, 300);
        getContentPane().add(rc);
        setResizable(false);
    }

    public static Consulta getInstance() {
    if(Consulta.instance == null ) {
        Consulta.instance = new Consulta(result);
        }
        return Consulta.instance;
    }
}


class Principal extends JFrame{

    public Principal(){
        // cria o painel principal com o tipo de layout
        // configura o painel para exibir  
        setSize(1000, 600);
		setLocation(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().add(PainelFrontal.getInstance());
        setResizable(false);
        setVisible(true);
        setLocationRelativeTo(null);
    }
}

class Agenda{
    public static void main(String[] args)
    {
        new Principal();
    }
}