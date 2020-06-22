package me.endergaming.dynamicmarketplace.commands;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    private final DynamicMarketplace plugin;
    List<BaseCommand> commandList = new ArrayList<>();
    public MarketCommand marketCmd;
    public BuyCommand buyCmd;
    public SellCommand sellCmd;
    public SellAllCommand sellAllCmd;
    public SellHandCommand sellHandCmd;
    public ItemInfoCommand itemInfoCmd;
    public WorthCommand worthCmd;
    public ReloadCommand reloadCmd;
    public CollectorCommand collectorCmd;

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
        reloadCmd = new ReloadCommand(plugin);
        collectorCmd = new CollectorCommand(plugin);

        // Register All Commands
        for (BaseCommand command : commandList) {
            command.register();
        }
    }
}
