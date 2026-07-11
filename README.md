# NextMarket

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/4f70e3b047e445ec8508694297e037a4)](https://app.codacy.com/gh/NextPlugins/NextMarket?utm_source=github.com&utm_medium=referral&utm_content=NextPlugins/NextMarket&utm_campaign=Badge_Grade_Settings)

Um sistema completo de mercado para servidores de Minecraft, organizado por menus, informações salvas em banco de dados SQL e com uma robusta [API](https://github.com/NextPlugins/NextMarket/blob/master/src/main/java/com/nextplugins/nextmarket/api/NextMarketAPI.java) para desenvolvedores. [Prints in-game](https://imgur.com/gallery/pfn8wBE).

## Compatibilidade

| | |
|--|--|
| **Minecraft** | **1.8.8 → 26.2** (Spigot / Paper e forks) |
| **Java do servidor** | Java 8 (1.8–1.16), Java 16+ (1.17), Java 17+ (1.18–1.20.4), Java 21+ (1.20.5+ / 26.x) |
| **Bytecode do plugin** | Java 8 (um único JAR para todas as versões) |

A compatibilidade multi-versão usa:

- [XSeries](https://github.com/CryptoMorin/XSeries) — materiais, enchantments e heads
- [NBT-API](https://github.com/tr7zw/Item-NBT-API) — tags NBT de categorias
- GUI própria em Bukkit (`Inventory` / `InventoryHolder`) — sem dependência de inventory-api abandonada
- Camada `compat` — detecção de versão, main hand, itens e skulls

> **Nota:** itens serializados no SQL são válidos **na mesma versão major do servidor**. Subir o servidor de 1.8 para 1.20 (ou 26.x) pode invalidar stacks antigos no banco — limitação do Bukkit, não do plugin.

## Comandos

| Comando | Descrição | Permissão |
|---------|-----------|-----------|
| `/mercado` | Exibe todos os comandos do plugin | `nextmarket.use` |
| `/mercado ver` | Exibe o menu de categorias do mercado | `nextmarket.use` |
| `/mercado vender <valor> [jogador]` | Coloque um item à venda | `nextmarket.use` |
| `/mercado pessoal` | Veja os itens anunciados especialmente para você | `nextmarket.use` |
| `/mercado anunciados` | Veja os itens que você anunciou no mercado | `nextmarket.use` |
| `/mercado info` | Informações do plugin (admin) | `nextmarket.admin` |

## Download

Você pode encontrar o plugin pronto para baixar [**aqui**](https://github.com/NextPlugins/NextMarket/releases), ou clonar o repositório e buildar:

```bash
./gradlew shadowJar
# artefato: build/libs/NextMarket-2.0.0.jar
```

## Configuração

O plugin conta com dois arquivos de configuração, `config.yml` e `categories.yml`.

### Materiais (`categories.yml`)

Nomes **modernos** e **legacy** são aceitos. O que não existir na versão do servidor é ignorado (com warning no console).

```yaml
icon:
  material: DIAMOND_SWORD   # ou nome legacy
  data: 0                   # só relevante em 1.8–1.12
  enchant: true
  inventorySlot: 10

configuration:
  materials:
    - DIAMOND_SWORD
    - WOOL:14               # data legacy
    - RED_WOOL              # equivalente moderno
    - WOOL:all              # ignora data
    - PLAYER_HEAD
    - SKULL_ITEM            # sinônimo legacy (1.8–1.12)
  names: []
  nbts: []
```

## Dependências

- [Vault](https://www.spigotmc.org/resources/vault.34315/) + plugin de economia

Dependências de desenvolvimento (XSeries, NBT-API, Guice, sql-provider, command-framework) vêm **shaded** no JAR.

### Tecnologias

- [Google Guice](https://github.com/google/guice) — injeção de dependência
- [command-framework](https://github.com/SaiintBrisson/command-framework) — comandos
- [sql-provider](https://github.com/henryfabio/sql-provider) — SQLite / MySQL
- [XSeries](https://github.com/CryptoMorin/XSeries) — multi-versão
- [NBT-API](https://github.com/tr7zw/Item-NBT-API) — NBT multi-versão
