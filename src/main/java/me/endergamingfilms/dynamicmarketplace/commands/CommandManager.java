package me.endergamingfilms.dynamicmarketplace.commands;

import me.endergamingfilms.dynamicmarketplace.DynamicMarketplace;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandManager {
    private final DynamicMarketplace plugin;
    public List<BaseCommand> commandList = new ArrayList<>();
    public List<String> subCommandList = new ArrayList<>();
    public Map<String, List<String>> soundexToWords;
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
        // Main command & sub-command
        commandList.add(collectorCmd = new CollectorCommand("collector", plugin));
        // Register Sub-Commands "/market command"
        reloadCmd = new ReloadCommand(plugin);
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
            if (command.command.equalsIgnoreCase("market")) continue;
            subCommandList.add(command.command);
        }
    }

    List<String> similarWords(String arg) {
        final String lowerCaseArg = arg.toLowerCase();
        return subCommandList.stream().map(String::toLowerCase).filter(s -> s.startsWith(lowerCaseArg)).collect(Collectors.toList());
    }
}
