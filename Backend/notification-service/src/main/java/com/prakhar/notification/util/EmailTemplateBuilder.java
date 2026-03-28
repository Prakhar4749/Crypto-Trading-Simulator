package com.prakhar.notification.util;

public class EmailTemplateBuilder {

  // ═══ EMAIL WRAPPER ═══
  public static String wrap(
      String appName, String logoUrl,
      String content, String footer) {
    return "<!DOCTYPE html><html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width'></head>" +
           "<body style='margin:0;padding:0;background-color:#f5f5f5;font-family:Arial,Helvetica,sans-serif;'>" +
           "<table width='100%' cellpadding='0' cellspacing='0' style='background-color:#f5f5f5;'>" +
           "<tr><td align='center' style='padding:20px 0;'>" +
           "<table width='600' cellpadding='0' cellspacing='0' style='background:#ffffff;border-radius:12px;box-shadow:0 4px 20px rgba(0,0,0,0.1);overflow:hidden;max-width:600px;width:100%;'>" +
           "<tr><td style='background:linear-gradient(135deg,#1a1a2e 0%,#16213e 100%);padding:30px;text-align:center;'>" +
           buildLogo(logoUrl, appName) +
           "<h1 style='color:#ffd700;margin:10px 0 0;font-size:28px;font-weight:bold;letter-spacing:2px;'>" + appName + "</h1>" +
           "<p style='color:#a0aec0;margin:5px 0 0;font-size:13px;'>Your Crypto Trading Simulator</p></td></tr>" +
           "<tr><td style='padding:40px 30px;'>" + content + "</td></tr>" +
           "<tr><td style='background:#1a1a2e;padding:20px 30px;text-align:center;'>" + footer + "</td></tr>" +
           "</table></td></tr></table></body></html>";
  }

  // ═══ LOGO ═══
  private static String buildLogo(
      String logoUrl, String appName) {
    if (logoUrl != null && !logoUrl.isBlank()) {
      return "<img src='" + logoUrl + "' alt='" + appName + "' style='height:50px;'>";
    }
    return "";
  }

  // ═══ GREETING ═══
  public static String greeting(
      String fullName) {
    return "<h2 style='color:#1a1a2e;margin:0 0 10px;font-size:22px;'>Hi " + fullName + "! 👋</h2>";
  }

  // ═══ PARAGRAPH ═══
  public static String para(String text) {
    return "<p style='color:#4a5568;font-size:15px;line-height:1.7;margin:0 0 16px;'>" + text + "</p>";
  }

  // ═══ CTA BUTTON ═══
  public static String ctaButton(
      String url, String label, 
      String color) {
    return "<table cellpadding='0' cellspacing='0' width='100%'><tr><td align='center' style='padding:20px 0;'>" +
           "<a href='" + url + "' style='background:" + color + ";color:#1a1a2e;padding:14px 32px;border-radius:8px;text-decoration:none;font-weight:bold;font-size:15px;display:inline-block;letter-spacing:0.5px;'>" +
           label + "</a></td></tr></table>";
  }

  // ═══ OTP BOX ═══
  public static String otpBox(String otp) {
    return "<table cellpadding='0' cellspacing='0' width='100%'><tr><td align='center' style='padding:20px 0;'>" +
           "<div style='background:#1a1a2e;border-radius:12px;padding:20px 40px;display:inline-block;'>" +
           "<p style='color:#a0aec0;font-size:12px;margin:0 0 8px;text-transform:uppercase;letter-spacing:2px;'>Your OTP</p>" +
           "<p style='color:#ffd700;font-size:36px;font-weight:bold;margin:0;letter-spacing:8px;font-family:monospace;'>" +
           otp + "</p></div></td></tr></table>";
  }

  // ═══ INFO BOX (highlight) ═══
  public static String infoBox(
      String title, String value) {
    return "<table cellpadding='0' cellspacing='0' width='100%' style='margin-bottom:16px;'><tr>" +
           "<td style='background:#f7f8fc;border-left:4px solid #ffd700;border-radius:0 8px 8px 0;padding:12px 16px;'>" +
           "<p style='color:#718096;font-size:12px;margin:0 0 4px;text-transform:uppercase;letter-spacing:1px;'>" + title + "</p>" +
           "<p style='color:#1a1a2e;font-size:18px;font-weight:bold;margin:0;'>" + value + "</p></td></tr></table>";
  }

  // ═══ WARNING BOX ═══
  public static String warningBox(
      String text) {
    return "<div style='background:#fff3cd;border:1px solid #ffc107;border-radius:8px;padding:12px 16px;margin:16px 0;color:#856404;font-size:13px;'>" +
           "⚠️ " + text + "</div>";
  }

  // ═══ DIVIDER ═══
  public static String divider() {
    return "<hr style='border:none;border-top:1px solid #e2e8f0;margin:24px 0;'>";
  }

  // ═══ FOOTER ═══
  public static String footer(
      String appName, String frontendUrl) {
    return "<p style='color:#718096;font-size:12px;margin:0 0 8px;'>© 2026 " + appName + ". All rights reserved.</p>" +
           "<p style='margin:8px 0;'><a href='" + frontendUrl + "' style='color:#ffd700;text-decoration:none;font-size:12px;'>Visit CoinDesk Platform</a></p>" +
           "<p style='color:#4a5568;font-size:11px;margin:8px 0 0;'>This is an automated message. Please do not reply to this email.</p>";
  }
}
