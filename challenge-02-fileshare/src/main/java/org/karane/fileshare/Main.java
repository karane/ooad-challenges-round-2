package org.karane.fileshare;

import org.karane.fileshare.crypto.AesEncryptionService;

import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        FileShareService service = new FileShareService(new AesEncryptionService(), Path.of("fileshare-data"));

        System.out.println("=== FileShare System ===\n");

        service.save("report.txt", "Q1 revenue: $1,200,000");
        service.save("notes.md", "# Meeting Notes\n- Follow up with team");
        service.save("budget.csv", "category,amount\nmarketing,5000\neng,30000");
        service.save("report-final.txt", "Final Q1 report: approved.");

        System.out.println("All files:");
        service.listFiles().forEach(f -> System.out.println("  " + f));

        System.out.println("\nSearch 'report':");
        service.search("report").forEach(f -> System.out.println("  " + f));

        System.out.println("\nRestore 'notes.md':");
        System.out.println(service.restore("notes.md"));

        service.delete("budget.csv");

        System.out.println("\nAfter deleting budget.csv:");
        service.listFiles().forEach(f -> System.out.println("  " + f));
    }
}
