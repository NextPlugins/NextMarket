# NextMarket

Um sistema completo de mercado para servidores de Minecraft, organizado por menus, informações salvas em banco de dados SQL e com uma robusta API para desenvolvedores. [Prints in-game](https://imgur.com/gallery/pfn8wBE).

## Comandos
|Comando         |Descrição                      |Permissão                    |
|----------------|-------------------------------|-----------------------------|
|/mercado        |Exibe todos os comandos do plugin|`nextmarket.use`           |
|/mercado ver    |Exibe o menu de categorias do mercado|`nextmarket.use`       |
|/mercado vender |Coloque um item à venda	     |`nextmarket.use`			   |
|/mercado pessoal|Veja os itens que anunciaram especialmente para você|`nextmarket.use`			   |
|/mercado anunciados|Veja os itens que você anunciou no mercado	     |`nextmarket.use`			   |

## Download

Você pode encontrar o plugin pronto para baixar [**aqui**](https://github.com/NextPlugins/NextMarket/releases), ou se você quiser, pode optar por clonar o repositório e dar build no plugin com suas alterações.

## Configuração

O plugin conta com dois arquivos de configuração `config.yml` e `categories.yml` em que você pode alterar mensagens, menus, categorias do mercado e outras configurações.

## Dependências
O NextMarket apenas precisa do [Vault](https://www.spigotmc.org/resources/vault.34315/) e de algum plugin de economia. As dependências de desenvolvimento serão baixadas automáticamente quando o plugin for habilitado pela primeira vez.

### Tecnologias usadas
- [Google Guice](https://github.com/google/guice) - Fornece suporte para injeção de dependência usando anotações.

**APIs e Frameworks**

- [command-framework](https://github.com/SaiintBrisson/command-framework) - Framework para criação e gerenciamento de comandos.
- [inventory-api](https://github.com/HenryFabio/inventory-api) - API para criação e o gerenciamento de inventários customizados.
- [sql-provider](https://github.com/henryfabio/sql-provider) - Provê a conexão com o banco de dados.
