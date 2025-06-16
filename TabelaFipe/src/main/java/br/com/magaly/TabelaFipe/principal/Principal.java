package br.com.magaly.TabelaFipe.principal;

import br.com.magaly.TabelaFipe.model.Dados;
import br.com.magaly.TabelaFipe.model.Modelos;
import br.com.magaly.TabelaFipe.model.Veiculo;
import br.com.magaly.TabelaFipe.service.ConsumoApi;
import br.com.magaly.TabelaFipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
   private Scanner leitura = new Scanner(System.in);
   private ConsumoApi consumo = new ConsumoApi();
   private ConverteDados conversor = new ConverteDados();

   private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu(){
        var menu = """
                *** OPÇÕES ***
                CARRO
                MOTO
                CAMINHÃO
                
                DIGITE UMA DAS OPÇÕES PARA CONSULTAR:
                """;
        System.out.println(menu);
        var opcao = leitura.nextLine();
        String endereco;

        if(opcao.toLowerCase().contains("carr")){
            endereco = URL_BASE + "carros/marcas";

        } else if (opcao.toLowerCase().contains("mot")) {
            endereco = URL_BASE + "motos/marcas";

        } else {
            endereco = URL_BASE + "caminhoes/marcas";
        }

        var json = consumo.obterDados(endereco);
        System.out.println(json);
        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("INFORME O CÓDIGO DA MARCA PARA A CONSULTA: ");
        var codigoMarca = leitura.nextLine();

        endereco = endereco + "/" + codigoMarca + "/modelos";
        json = consumo.obterDados(endereco);
        var modeloLista = conversor.obterDados(json, Modelos.class);

        System.out.println("\nMODELOS DESSA MARCA: ");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nDIGITE UM TRECHO DO NOME DO VEICULO A SER BUSCADO");
        var nomeVeiculo = leitura.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nMODELOS FILTRADOS: ");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("DIGITE O CÓDIGO DO MODELO PARA BUSCAR OS VALORES DE AVALIAÇÃO: ");
        var codigoModelo = leitura.nextLine();

        endereco = endereco + "/" + codigoModelo + "/anos";
        json = consumo.obterDados(endereco);
        List<Dados> anos = conversor.obterLista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for( int i = 0; i< anos.size(); i++) {
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\nTODOS OS VEICULOS FILTRADOS COM AVALIAÇÕES POR ANO: ");
        veiculos.forEach(System.out::println);

    }
}
