package com.cometproject.server.game.commands.staff;

import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.network.sessions.Session;

public class CloseDiceCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        for (RoomItemFloor item : client.getPlayer().getEntity().getRoom().getItems().getByInteraction("dice")) {
            item.getItemData().setData("0");
            item.sendUpdate();
            item.saveData();
        }
    }

    @Override
    public String getPermission() {
        return "cd_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Closes dices";
    }
}