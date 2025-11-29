package cc.ranmc.city.util;

public class VipUtil {

    public static int durationToDays(String s) {
        int years = 0, months = 0, weeks = 0, days = 0;
        int hours = 0, minutes = 0, seconds = 0;

        String[] parts = s.split("\\s+");
        for (String p : parts) {
            if (p.endsWith("y"))   years   = Integer.parseInt(p.replace("y", ""));
            else if (p.endsWith("mo")) months = Integer.parseInt(p.replace("mo", ""));
            else if (p.endsWith("w")) weeks  = Integer.parseInt(p.replace("w", ""));
            else if (p.endsWith("d")) days   = Integer.parseInt(p.replace("d", ""));
            else if (p.endsWith("h")) hours  = Integer.parseInt(p.replace("h", ""));
            else if (p.endsWith("m")) minutes= Integer.parseInt(p.replace("m", ""));
            else if (p.endsWith("s")) seconds= Integer.parseInt(p.replace("s", ""));
        }

        // 1y = 365d, 1mo = 30d (常用约定), 1w = 7d
        double totalDays = 0;
        totalDays += years * 365;
        totalDays += months * 30;
        totalDays += weeks * 7;
        totalDays += days;
        totalDays += hours / 24.0;
        totalDays += minutes / 1440.0;
        totalDays += seconds / 86400.0;

        // 不足 1 天算 1 天
        return (int) Math.ceil(totalDays);
    }

}
