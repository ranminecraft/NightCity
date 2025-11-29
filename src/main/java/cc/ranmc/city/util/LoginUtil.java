package cc.ranmc.city.util;

import cc.baka9.catseedlogin.bukkit.CatSeedLoginAPI;
import com.viaversion.viaversion.api.Via;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.entity.Player;

import java.util.List;

public class LoginUtil {

    public static void sendDialog(Player player) {
        if (Via.getAPI().getPlayerVersion(player) < 771) return;
        if (CatSeedLoginAPI.isRegister(player.getName())) {
            player.showDialog(Dialog.create(builder -> builder
                    .empty()
                    .base(DialogBase.builder(Component.text("登陆账号 : " + player.getName()))
                            .canCloseWithEscape(false)
                            .inputs(List.of(
                                    DialogInput.text("password", Component.text("登陆密码")).build()
                            )).build()
                    )
                    .type(DialogType.notice(ActionButton.builder(Component.text("确认"))
                            .action(DialogAction.customClick((response, audience) -> {
                                String pwd = response.getText("password");
                                player.chat("/l " + pwd);
                            }, ClickCallback.Options.builder().lifetime(ClickCallback.DEFAULT_LIFETIME).build())).build()
                    ))));
        } else {
            player.showDialog(Dialog.create(builder -> builder
                    .empty()
                    .base(DialogBase.builder(Component.text("注册账号 : " + player.getName()))
                            .canCloseWithEscape(false)
                            .inputs(List.of(
                                    DialogInput.text("password", Component.text("密码")).build(),
                                    DialogInput.text("password2", Component.text("重复密码")).build()
                            )).build()
                    )
                    .type(DialogType.notice(ActionButton.builder(Component.text("确认"))
                            .action(DialogAction.customClick((response, audience) -> {
                                String pwd = response.getText("password");
                                String pwd2 = response.getText("password2");
                                player.chat("/reg " + pwd + " " + pwd2);
                            }, ClickCallback.Options.builder().lifetime(ClickCallback.DEFAULT_LIFETIME).build())).build()
                    ))));
        }
    }

}
