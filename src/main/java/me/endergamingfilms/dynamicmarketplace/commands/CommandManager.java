package me.endergamingfilms.dynamicmarketplace.commands;

import me.endergamingfilms.dynamicmarketplace.DynamicMarketplace;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    private final DynamicMarketplace plugin;
    public List<BaseCommand> commandList = new ArrayList<>();
    public List<String> subCommandList = new ArrayList<>();
    public MarketCommand marketCmd;
    public BuyCommand buyCmd;
    public SellCommand sellCmd;
    public SellAllCommand sellAllCmd;
    public SellHandCommand sellHandCmd;
    public ItemInfoCommand itemInfoCmd;
    public WorthCommand worthCmd;
    public ReloadCommand reloadCmd;
    public CollectorCommand collectorCmd;
    public StandingCommand standingCmd;

    public CommandManager(@NotNull final DynamicMarketplace instance) {
        this.plugin = instance;
    }

    public void registerCommands() {
        // Make Commands Accessible
        commandList.add(marketCmd = new MarketCommand("market", plugin));
        commandList.add(buyCmd = new BuyCommand("buy", plugin));
        commandList.add(sellCmd = new SellCommand("sell", plugin));
        commandList.add(sellAllCmd = new SellAllCommand("sellall", plugin));
        commandList.add(sellHandCmd = new SellHandCommand("sellhand", plugin));
        commandList.add(itemInfoCmd = new ItemInfoCommand("iteminfo", plugin));
        commandList.add(worthCmd = new WorthCommand("worth", plugin));

        // Register Sub-Commands "/market command"
        reloadCmd = new ReloadCommand(plugin);
        collectorCmd = new CollectorCommand(plugin);
        standingCmd = new StandingCommand(plugin);
        // Make command list
        subCommandList.add("reload");
        subCommandList.add("load");
        subCommandList.add("help");
        subCommandList.add("collector");
        subCommandList.add("standing");

        // Register BaseCommands "/command"
        for (BaseCommand command : commandList) {
            command.register();
        }
    }
}
