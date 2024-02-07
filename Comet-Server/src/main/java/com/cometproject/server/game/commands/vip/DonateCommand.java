package com.cometproject.server.game.commands.vip;

import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.players.data.PlayerData;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerDao;
import org.apache.commons.lang.StringUtils;

public class DonateCommand extends ChatCommand {

    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 3) {
            sendWhisper("Verkeerd aantal parameters (check de command list)", client);
            return;
        }

        String playerName = params[0];
        String strAmount = params[1];
        String currency = params[2];
        PlayerData playerData = PlayerDao.getDataByUsername(playerName);

        if (playerData == null) {
            sendWhisper("Gebruiker met de naam " + playerName + " is niet gevonden", client);
            return;
        }

        if (!StringUtils.isNumeric(strAmount)) {
            sendWhisper("Aantal moet een nummer zijn", client);
            return;
        }

        int amount = Integer.parseInt(strAmount);
        Session targetSession = NetworkManager.getInstance().getSessions().getByPlayerUsername(playerName);

        switch (currency) {
            case "credits":
                if (client.getPlayer().getData().getCredits() < amount) {
                    sendWhisper("Te weinig credits om te doneren", client);
                    return;
                }

                client.getPlayer().getData().decreaseCredits(amount);
                client.getPlayer().getData().save();

                client.send(client.getPlayer().composeCreditBalance());

                if (targetSession != null) {
                    targetSession.getPlayer().getData().increaseCredits(amount);
                    targetSession.getPlayer().getData().save();

                    targetSession.send(targetSession.getPlayer().composeCreditBalance());
                } else {
                    playerData.increaseCredits(amount);
                    playerData.save();
                }
                break;
            case "duckets":
                if (client.getPlayer().getData().getActivityPoints() < amount) {
                    sendWhisper("Te weinig duckets om te doneren", client);
                    return;
                }

                client.getPlayer().getData().decreaseActivityPoints(amount);
                client.getPlayer().getData().save();

                client.send(client.getPlayer().composeCurrenciesBalance());

                if (targetSession != null) {
                    targetSession.getPlayer().getData().increaseActivityPoints(amount);
                    targetSession.getPlayer().getData().save();

                    targetSession.send(targetSession.getPlayer().composeCurrenciesBalance());
                } else {
                    playerData.increaseActivityPoints(amount);
                    playerData.save();
                }
                break;
            case "diamanten":
                if (client.getPlayer().getData().getVipPoints() < amount) {
                    sendWhisper("Te weinig diamanten om te doneren", client);
                    return;
                }

                client.getPlayer().getData().decreaseVipPoints(amount);
                client.getPlayer().getData().save();

                client.send(client.getPlayer().composeCurrenciesBalance());

                if (targetSession != null) {
                    targetSession.getPlayer().getData().increaseVipPoints(amount);
                    targetSession.getPlayer().getData().save();

                    targetSession.send(targetSession.getPlayer().composeCurrenciesBalance());
                } else {
                    playerData.increaseVipPoints(amount);
                    playerData.save();
                }
                break;
            default:
                sendWhisper("Currency moet een van deze waardes zijn: credits, duckets of diamanten", client);
                return;
        }

        sendWhisper("Je hebt " + amount + " " + currency + " gedoneerd aan " + playerName, client);

        if (targetSession != null) {
            sendWhisper(client.getPlayer().getData().getUsername() + " heeft jou " + amount + " " + currency + " gedoneerd", targetSession);
        }
    }

    @Override
    public String getPermission() {
        return "donate_command";
    }

    @Override
    public String getParameter() {
        return "%spelernaam% %aantal% %currency%";
    }

    @Override
    public String getDescription() {
        return "Doneer een bepaald aantal currency aan een bepaalde speler.";
    }
}