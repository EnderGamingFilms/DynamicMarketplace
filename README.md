## Commands
<code>/market [reload/load] - Either load materials.yml or reload all files</code><br />
<code>/market standing [add/remove/set] [playerName] \<amount> - Adjust a player's standing</code><br />
<code>/market collector [playerName] - Opens the collector gui</code><br />
<code>/buy \<item> \<amount> - Buy an item from the market</code><br />
<code>/sell \<item> \<amount> - Sells an item to the market</code><br />
<code>/sellall \<item> - Sells all of an item in your inventory</code><br />
<code>/iteminfo \<item> - Get all market info on a specific item</code><br />
<code>/collector - Open collector for yourself</code><br />
<code>/worth - Get the sale price of the held item</code><br />
<code>/sellhand - Sells the item in your hand and that amount</code>/>

## Permissions
<code>market.\*</code> - Admin<br />
<code>market.command.\*</code> - Admin<br />
<code>market.reload</code> - Admin<br />
<code>market.command.buy</code> - Admin<br />
<code>market.command.sell</code> - Admin<br />
<code>market.command.sellall</code> - Admin<br />
<code>market.command.sellhand</code> - Admin<br />
<code>market.command.collector</code> - Admin<br />
<code>market.command.collector.others</code> - Admin<br />
<code>market.command.standing</code> - Admin<br />
<code>market.command.info</code> - Admin<br />
<code>market.command.worth</code> - Admin<br />

## PlaceHolderAPI Compatible
Make sure **PAPI** is installed! Download and install the latest [PlaceHolderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
### <ins>PlaceHolders:</ins>
> <code>%market_buy_{material},{amount}% >> returns items buy price</code>

> <code>%market_sell_{material},{amount}% >> returns items sell price</code>

> <code>%market_friendly_{material}% >> returns non-namespaced item name</code>

> <code>%market_amount_{material}% >> returns amount of item in the market</code>

> <code>%market_hand% >> returns the sell price of held item</code>

> <code>%market_standing% >> returns current player's standing</code>

> <code>%market_default_standing% >> returns default player standing</code>

## Requirements
### • Vault [\[link\]](https://www.spigotmc.org/resources/vault.34315/)
### • Any vault compatible economy plugin
### • PAPI (optional) [\[link\]](https://www.spigotmc.org/resources/placeholderapi.6245/)