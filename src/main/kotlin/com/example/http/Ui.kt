package com.example.http

import kotlinx.html.HEAD
import kotlinx.html.meta
import kotlinx.html.style
import kotlinx.html.unsafe

/**
 * Общий набор стилей и метаданных для страниц, чтобы интерфейс выглядел аккуратно и
 * корректно отображал русские символы.
 */
fun HEAD.commonMetaAndStyles() {
    meta { charset = "UTF-8" }
    style {
        unsafe {
            raw(
                """
                :root {
                  --bg: #f6f7fb;
                  --card: #ffffff;
                  --border: #e2e8f0;
                  --text: #0f172a;
                  --muted: #475569;
                  --primary: #1f6feb;
                  --primary-light: #e8f0ff;
                  --success: #12a150;
                  --warning: #d97706;
                  --danger: #dc2626;
                }
                * { box-sizing: border-box; }
                body {
                  margin: 0;
                  background: var(--bg);
                  color: var(--text);
                  font-family: "Segoe UI", "Inter", system-ui, sans-serif;
                  line-height: 1.5;
                }
                a { color: var(--primary); text-decoration: none; }
                a:hover { text-decoration: underline; }
                .page { max-width: 1180px; margin: 0 auto; padding: 28px 18px 48px; }
                .card {
                  background: var(--card);
                  border: 1px solid var(--border);
                  border-radius: 12px;
                  padding: 18px 20px;
                  margin-bottom: 18px;
                  box-shadow: 0 6px 16px rgba(15, 23, 42, 0.06);
                }
                h1, h2, h3 { margin-top: 0; }
                .stack { display: flex; gap: 12px; flex-wrap: wrap; align-items: center; }
                .nav { display: flex; gap: 12px; flex-wrap: wrap; margin-bottom: 16px; }
                .btn, button {
                  display: inline-flex;
                  align-items: center;
                  gap: 6px;
                  padding: 8px 14px;
                  border-radius: 10px;
                  border: 1px solid var(--primary);
                  background: var(--primary);
                  color: #fff;
                  font-weight: 600;
                  cursor: pointer;
                  transition: all 0.15s ease;
                }
                .btn.secondary, button.secondary {
                  background: #fff;
                  color: var(--primary);
                  border-color: var(--primary);
                }
                .btn.danger, button.danger {
                  background: var(--danger);
                  border-color: var(--danger);
                }
                .btn:hover, button:hover { box-shadow: 0 6px 16px rgba(31, 111, 235, 0.25); }
                table { width: 100%; border-collapse: collapse; }
                th, td { padding: 10px 12px; border: 1px solid var(--border); vertical-align: top; }
                th { background: #f0f4f8; text-align: left; }
                form p { margin: 0 0 12px; }
                input, select, textarea {
                  width: 100%;
                  max-width: 480px;
                  padding: 9px 10px;
                  border: 1px solid var(--border);
                  border-radius: 8px;
                  font-size: 15px;
                }
                textarea { min-height: 96px; resize: vertical; }
                .muted { color: var(--muted); }
                .badge { padding: 4px 10px; border-radius: 999px; font-size: 13px; font-weight: 600; display: inline-block; }
                .status-assigned { background: var(--primary-light); color: var(--primary); }
                .status-downloaded { background: #e0f2fe; color: #075985; }
                .status-completed { background: #dcfce7; color: var(--success); }
                .tag-overdue { color: var(--danger); font-weight: 600; margin-left: 6px; }
                .alert { background: #fff7ed; border: 1px solid #fed7aa; color: #9a3412; padding: 10px 12px; border-radius: 10px; }
                .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 12px; }
                ul { padding-left: 18px; }
                hr { border: none; border-top: 1px solid var(--border); margin: 20px 0; }
              """
            )
        }
    }
}
