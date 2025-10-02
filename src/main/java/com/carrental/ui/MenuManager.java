package com.carrental.ui;

import com.carrental.dao.DepositDAO;
import com.carrental.dao.RentalDAO;
import com.carrental.exception.AuthenticationException;
import com.carrental.exception.BusinessRuleException;
import com.carrental.exception.VehicleNotFoundException;
import com.carrental.model.*;
import com.carrental.model.enums.*;
import com.carrental.service.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main menu manager for the vehicle rental application
 */
public class MenuManager {
    
    private final AuthService authService;
    private final VehicleService vehicleService;
    private final RentalService rentalService;
    private boolean running = true;
    
    public MenuManager() throws SQLException {
        this.authService = new AuthService();
        this.vehicleService = new VehicleService();
        this.rentalService = new RentalService();
    }
    
    public void start() {
        ConsoleUI.printHeader("ARAÇ KİRALAMA SİSTEMİNE HOŞGELDİNİZ");
        
        while (running) {
            try {
                if (!authService.isLoggedIn()) {
                    showLoginMenu();
                } else if (authService.isAdmin()) {
                    showAdminMenu();
                } else {
                    showCustomerMenu();
                }
            } catch (Exception e) {
                ConsoleUI.printError("Bir hata oluştu: " + e.getMessage());
                ConsoleUI.waitForEnter();
            }
        }
        
        ConsoleUI.printInfo("Sistemden çıkılıyor... Güle güle!");
        ConsoleUI.close();
    }
    
    private void showLoginMenu() throws SQLException, BusinessRuleException {
        ConsoleUI.printHeader("GİRİŞ / KAYIT");
        System.out.println("1. Giriş Yap");
        System.out.println("2. Kayıt Ol");
        System.out.println("0. Çıkış");
        ConsoleUI.printSeparator();
        
        int choice = ConsoleUI.readInt("Seçiminiz");
        
        switch (choice) {
            case 1 -> login();
            case 2 -> register();
            case 0 -> running = false;
            default -> ConsoleUI.printError("Geçersiz seçim!");
        }
    }
    
    private void login() {
        try {
            ConsoleUI.printHeader("GİRİŞ YAP");
            String email = ConsoleUI.readString("E-posta");
            String password = ConsoleUI.readString("Şifre");
            
            User user = authService.login(email, password);
            ConsoleUI.printSuccess("Giriş başarılı! Hoş geldiniz, " + user.getFirstName());
            ConsoleUI.waitForEnter();
        } catch (AuthenticationException | SQLException e) {
            ConsoleUI.printError(e.getMessage());
            ConsoleUI.waitForEnter();
        }
    }
    
    private void register() throws SQLException, BusinessRuleException {
        ConsoleUI.printHeader("KAYIT OL");
        String firstName = ConsoleUI.readString("Ad");
        String lastName = ConsoleUI.readString("Soyad");
        String email = ConsoleUI.readString("E-posta");
        String password = ConsoleUI.readString("Şifre (min 6 karakter)");
        String phone = ConsoleUI.readString("Telefon");
        
        System.out.println("\nDoğum Tarihi:");
        int year = ConsoleUI.readInt("Yıl (örn: 1990)");
        int month = ConsoleUI.readInt("Ay (1-12)");
        int day = ConsoleUI.readInt("Gün (1-31)");
        LocalDate birthDate = LocalDate.of(year, month, day);
        
        System.out.println("\nHesap Tipi:");
        System.out.println("1. Bireysel");
        System.out.println("2. Kurumsal");
        int roleChoice = ConsoleUI.readInt("Seçiminiz");
        UserRole role = roleChoice == 2 ? UserRole.CORPORATE : UserRole.INDIVIDUAL;
        
        User user = authService.register(firstName, lastName, email, password, phone, birthDate, role);
        ConsoleUI.printSuccess("Kayıt başarılı! Artık giriş yapabilirsiniz.");
        ConsoleUI.waitForEnter();
    }
    
    private void showAdminMenu() throws SQLException, BusinessRuleException, VehicleNotFoundException {
        ConsoleUI.printHeader("ADMIN PANEL");
        System.out.println("1. Araç Ekle");
        System.out.println("2. Araç Listele");
        System.out.println("3. Araç Güncelle");
        System.out.println("4. Araç Sil");
        System.out.println("5. Tüm Kiralamaları Görüntüle");
        System.out.println("6. Tüm Depozito Kayıtlarını Görüntüle");
        System.out.println("7. Depozito Durumu Güncelle");
        System.out.println("0. Çıkış Yap");
        ConsoleUI.printSeparator();
        
        int choice = ConsoleUI.readInt("Seçiminiz");
        
        switch (choice) {
            case 1 -> addVehicle();
            case 2 -> listVehicles();
            case 3 -> updateVehicle();
            case 4 -> deleteVehicle();
            case 5 -> viewAllRentals();
            case 6 -> viewAllDeposits();
            case 7 -> updateDepositStatus();
            case 0 -> {
                authService.logout();
                ConsoleUI.printSuccess("Çıkış yapıldı");
            }
            default -> ConsoleUI.printError("Geçersiz seçim!");
        }
        ConsoleUI.waitForEnter();
    }
    
    private void showCustomerMenu() throws SQLException, BusinessRuleException, VehicleNotFoundException {
        ConsoleUI.printHeader("MÜŞTERİ PANEL");
        System.out.println("1. Araç Ara ve Kirala");
        System.out.println("2. Kiralamalarımı Görüntüle");
        System.out.println("3. Kiralama İptal Et");
        System.out.println("4. Depozito Bilgilerimi Görüntüle");
        System.out.println("0. Çıkış Yap");
        ConsoleUI.printSeparator();
        
        int choice = ConsoleUI.readInt("Seçiminiz");
        
        switch (choice) {
            case 1 -> searchAndRentVehicle();
            case 2 -> viewMyRentals();
            case 3 -> cancelRental();
            case 4 -> viewMyDeposits();
            case 0 -> {
                authService.logout();
                ConsoleUI.printSuccess("Çıkış yapıldı");
            }
            default -> ConsoleUI.printError("Geçersiz seçim!");
        }
        ConsoleUI.waitForEnter();
    }
    
    private void addVehicle() throws SQLException, BusinessRuleException {
        ConsoleUI.printHeader("YENİ ARAÇ EKLE");
        
        System.out.println("Araç Tipi:");
        System.out.println("1. Otomobil");
        System.out.println("2. Helikopter");
        System.out.println("3. Motosiklet");
        int typeChoice = ConsoleUI.readInt("Seçiminiz");
        
        VehicleType type = switch (typeChoice) {
            case 1 -> VehicleType.AUTOMOBILE;
            case 2 -> VehicleType.HELICOPTER;
            case 3 -> VehicleType.MOTORCYCLE;
            default -> throw new BusinessRuleException("Geçersiz araç tipi");
        };
        
        String brand = ConsoleUI.readString("Marka");
        String model = ConsoleUI.readString("Model");
        int year = ConsoleUI.readInt("Yıl");
        String color = ConsoleUI.readString("Renk");
        String plateNumber = ConsoleUI.readString("Plaka");
        double value = ConsoleUI.readDouble("Değer (TL)");
        int capacity = ConsoleUI.readInt("Kapasite");
        String enginePower = ConsoleUI.readString("Motor Gücü");
        String fuelType = ConsoleUI.readString("Yakıt Tipi");
        String transmission = ConsoleUI.readString("Vites Tipi");
        
        Vehicle vehicle = vehicleService.createVehicle(type, brand, model, year, color, 
                                                     plateNumber, BigDecimal.valueOf(value), 
                                                     capacity, enginePower, fuelType, transmission);
        
        ConsoleUI.printSuccess("Araç başarıyla eklendi! ID: " + vehicle.getId());
    }
    
    private void listVehicles() throws SQLException {
        ConsoleUI.printHeader("ARAÇ LİSTESİ");
        List<Vehicle> allVehicles = vehicleService.getAllVehicles();
        
        if (allVehicles.isEmpty()) {
            ConsoleUI.printInfo("Henüz araç bulunmamaktadır.");
            return;
        }
        
        int currentPage = 1;
        int pageSize = 5;
        int totalPages = (int) Math.ceil((double) allVehicles.size() / pageSize);
        
        while (true) {
            int startIndex = (currentPage - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, allVehicles.size());
            
            System.out.println("\n=== Sayfa " + currentPage + " / " + totalPages + " ===\n");
            
            for (int i = startIndex; i < endIndex; i++) {
                Vehicle vehicle = allVehicles.get(i);
                System.out.println("\nID: " + vehicle.getId());
                System.out.println(vehicle.getVehicleInfo());
                System.out.println("Plaka: " + vehicle.getPlateNumber());
                System.out.println("Değer: " + vehicle.getValueTl() + " TL");
                System.out.println("Durum: " + (vehicle.isAvailable() ? "Müsait" : "Kirada"));
                System.out.println("Günlük Fiyat: " + vehicle.getDailyRate() + " TL");
                ConsoleUI.printSeparator();
            }
            
            System.out.println("\nNavigasyon:");
            if (currentPage < totalPages) {
                System.out.println("N - Sonraki Sayfa");
            }
            if (currentPage > 1) {
                System.out.println("P - Önceki Sayfa");
            }
            System.out.println("0 - Geri Dön");
            
            String choice = ConsoleUI.readString("Seçiminiz");
            
            if (choice.equalsIgnoreCase("N") && currentPage < totalPages) {
                currentPage++;
            } else if (choice.equalsIgnoreCase("P") && currentPage > 1) {
                currentPage--;
            } else if (choice.equals("0")) {
                break;
            }
        }
    }
    
    private void updateVehicle() throws SQLException, VehicleNotFoundException, BusinessRuleException {
        long vehicleId = ConsoleUI.readLong("Güncellenecek Araç ID");
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
        
        ConsoleUI.printInfo("Mevcut Bilgiler: " + vehicle.getVehicleInfo());
        System.out.println("Yeni bilgileri giriniz (boş bırakmak için Enter basın):");
        
        String brand = ConsoleUI.readString("Yeni Marka");
        if (!brand.isEmpty()) vehicle.setBrand(brand);
        
        String model = ConsoleUI.readString("Yeni Model");
        if (!model.isEmpty()) vehicle.setModel(model);
        
        vehicleService.updateVehicle(vehicle);
        ConsoleUI.printSuccess("Araç başarıyla güncellendi!");
    }
    
    private void deleteVehicle() throws SQLException, VehicleNotFoundException {
        long vehicleId = ConsoleUI.readLong("Silinecek Araç ID");
        vehicleService.deleteVehicle(vehicleId);
        ConsoleUI.printSuccess("Araç başarıyla silindi!");
    }
    
    private void searchAndRentVehicle() throws SQLException, BusinessRuleException, VehicleNotFoundException {
        ConsoleUI.printHeader("ARAÇ ARA VE KİRALA");
        
        int currentPage = 1;
        int pageSize = 5;
        boolean continueSearching = true;
        
        while (continueSearching) {
            List<Vehicle> vehicles = vehicleService.searchVehicles(null, null, null, null, true, currentPage, pageSize);
            
            if (vehicles.isEmpty()) {
                if (currentPage == 1) {
                    ConsoleUI.printInfo("Müsait araç bulunmamaktadır.");
                    return;
                } else {
                    ConsoleUI.printInfo("Daha fazla araç bulunmamaktadır.");
                    currentPage--;
                    continue;
                }
            }
            
            System.out.println("\n=== Sayfa " + currentPage + " ===");
            System.out.println("Müsait Araçlar:");
            for (Vehicle vehicle : vehicles) {
                System.out.println("\nID: " + vehicle.getId() + " - " + vehicle.getVehicleInfo());
                System.out.println("Günlük: " + vehicle.getDailyRate() + " TL | Haftalık: " + 
                                 vehicle.getWeeklyRate() + " TL | Aylık: " + vehicle.getMonthlyRate() + " TL");
                if (vehicle.requiresDeposit()) {
                    System.out.println("⚠ Depozito Gerekli: " + vehicle.getDepositAmount() + " TL (30 yaş üstü)");
                }
                ConsoleUI.printSeparator();
            }
            
            System.out.println("\nSeçenekler:");
            System.out.println("1. Araç Kirala");
            System.out.println("2. Sonraki Sayfa");
            if (currentPage > 1) {
                System.out.println("3. Önceki Sayfa");
            }
            System.out.println("0. Geri Dön");
            
            int choice = ConsoleUI.readInt("Seçiminiz");
            
            if (choice == 1) {
                continueSearching = false;
                // Kiralama işlemine devam et
            } else if (choice == 2) {
                currentPage++;
                continue;
            } else if (choice == 3 && currentPage > 1) {
                currentPage--;
                continue;
            } else if (choice == 0) {
                return;
            }
        }
        
        long vehicleId = ConsoleUI.readLong("\nKiralamak istediğiniz aracın ID'si");
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
        
        System.out.println("\nKiralama Süresi:");
        System.out.println("1. Saatlik");
        System.out.println("2. Günlük");
        System.out.println("3. Haftalık");
        System.out.println("4. Aylık");
        int pricingChoice = ConsoleUI.readInt("Seçiminiz");
        
        PricingType pricingType = switch (pricingChoice) {
            case 1 -> PricingType.HOURLY;
            case 2 -> PricingType.DAILY;
            case 3 -> PricingType.WEEKLY;
            case 4 -> PricingType.MONTHLY;
            default -> throw new BusinessRuleException("Geçersiz fiyatlandırma tipi");
        };
        
        int duration = ConsoleUI.readInt("Süre (birim: " + pricingType.getDisplayName() + ")");
        
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = startDate.plusHours(pricingType.getHoursMultiplier() * duration);
        
        BigDecimal totalAmount = vehicle.calculateRentalCost(pricingType, duration);
        BigDecimal depositAmount = vehicle.getDepositAmount();
        
        System.out.println("\n--- Kiralama Özeti ---");
        System.out.println("Toplam Tutar: " + totalAmount + " TL");
        if (depositAmount.compareTo(BigDecimal.ZERO) > 0) {
            System.out.println("Depozito: " + depositAmount + " TL");
        }
        
        String confirm = ConsoleUI.readString("\nOnaylıyor musunuz? (E/H)");
        if (confirm.equalsIgnoreCase("E")) {
            Rental rental = rentalService.createRental(authService.getCurrentUser(), vehicleId, 
                                                     startDate, endDate, pricingType, duration);
            ConsoleUI.printSuccess("Kiralama başarılı! Kiralama ID: " + rental.getId());
        } else {
            ConsoleUI.printInfo("Kiralama iptal edildi.");
        }
    }
    
    private void viewMyRentals() throws SQLException {
        ConsoleUI.printHeader("KİRALAMALARIM");
        List<Rental> allRentals = rentalService.getUserRentals(authService.getCurrentUser().getId());
        
        if (allRentals.isEmpty()) {
            ConsoleUI.printInfo("Henüz kiralama bulunmamaktadır.");
            return;
        }
        
        int currentPage = 1;
        int pageSize = 5;
        int totalPages = (int) Math.ceil((double) allRentals.size() / pageSize);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        while (true) {
            int startIndex = (currentPage - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, allRentals.size());
            
            System.out.println("\n=== Sayfa " + currentPage + " / " + totalPages + " ===\n");
            
            for (int i = startIndex; i < endIndex; i++) {
                Rental rental = allRentals.get(i);
                System.out.println("\nKiralama ID: " + rental.getId());
                
                // Araç bilgilerini getir
                try {
                    Vehicle vehicle = vehicleService.getVehicleById((long) rental.getVehicleId());
                    System.out.println("Araç: " + vehicle.getBrand() + " " + vehicle.getModel() + 
                                     " (" + vehicle.getYear() + ")");
                    System.out.println("Plaka: " + vehicle.getPlateNumber());
                } catch (VehicleNotFoundException e) {
                    System.out.println("Araç ID: " + rental.getVehicleId());
                }
                
                System.out.println("Başlangıç: " + rental.getStartDate().format(formatter));
                System.out.println("Bitiş: " + rental.getEndDate().format(formatter));
                System.out.println("Tutar: " + rental.getTotalAmount() + " TL");
                if (rental.getDepositAmount().compareTo(BigDecimal.ZERO) > 0) {
                    System.out.println("Depozito: " + rental.getDepositAmount() + " TL");
                }
                System.out.println("Durum: " + rental.getStatus().getDisplayName());
                ConsoleUI.printSeparator();
            }
            
            System.out.println("\nNavigasyon:");
            if (currentPage < totalPages) {
                System.out.println("N - Sonraki Sayfa");
            }
            if (currentPage > 1) {
                System.out.println("P - Önceki Sayfa");
            }
            System.out.println("0 - Geri Dön");
            
            String choice = ConsoleUI.readString("Seçiminiz");
            
            if (choice.equalsIgnoreCase("N") && currentPage < totalPages) {
                currentPage++;
            } else if (choice.equalsIgnoreCase("P") && currentPage > 1) {
                currentPage--;
            } else if (choice.equals("0")) {
                break;
            }
        }
    }
    
    private void cancelRental() throws SQLException, BusinessRuleException {
        long rentalId = ConsoleUI.readLong("İptal edilecek Kiralama ID");
        rentalService.cancelRental(rentalId);
        ConsoleUI.printSuccess("Kiralama başarıyla iptal edildi!");
    }
    
    private void viewAllRentals() throws SQLException {
        ConsoleUI.printHeader("TÜM KİRALAMALAR");
        List<Rental> allRentals = rentalService.getAllRentals();
        
        if (allRentals.isEmpty()) {
            ConsoleUI.printInfo("Henüz kiralama bulunmamaktadır.");
            return;
        }
        
        int currentPage = 1;
        int pageSize = 5;
        int totalPages = (int) Math.ceil((double) allRentals.size() / pageSize);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        while (true) {
            int startIndex = (currentPage - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, allRentals.size());
            
            System.out.println("\n=== Sayfa " + currentPage + " / " + totalPages + " ===\n");
            
            for (int i = startIndex; i < endIndex; i++) {
                Rental rental = allRentals.get(i);
                System.out.println("\nKiralama ID: " + rental.getId());
                System.out.println("Kullanıcı ID: " + rental.getUserId());
                
                // Araç bilgilerini getir
                try {
                    Vehicle vehicle = vehicleService.getVehicleById((long) rental.getVehicleId());
                    System.out.println("Araç: " + vehicle.getBrand() + " " + vehicle.getModel() + 
                                     " (" + vehicle.getYear() + ")");
                    System.out.println("Plaka: " + vehicle.getPlateNumber());
                } catch (VehicleNotFoundException e) {
                    System.out.println("Araç ID: " + rental.getVehicleId());
                }
                
                System.out.println("Başlangıç: " + rental.getStartDate().format(formatter));
                System.out.println("Bitiş: " + rental.getEndDate().format(formatter));
                System.out.println("Tutar: " + rental.getTotalAmount() + " TL");
                if (rental.getDepositAmount().compareTo(BigDecimal.ZERO) > 0) {
                    System.out.println("Depozito: " + rental.getDepositAmount() + " TL");
                }
                System.out.println("Durum: " + rental.getStatus().getDisplayName());
                ConsoleUI.printSeparator();
            }
            
            System.out.println("\nNavigasyon:");
            if (currentPage < totalPages) {
                System.out.println("N - Sonraki Sayfa");
            }
            if (currentPage > 1) {
                System.out.println("P - Önceki Sayfa");
            }
            System.out.println("0 - Geri Dön");
            
            String choice = ConsoleUI.readString("Seçiminiz");
            
            if (choice.equalsIgnoreCase("N") && currentPage < totalPages) {
                currentPage++;
            } else if (choice.equalsIgnoreCase("P") && currentPage > 1) {
                currentPage--;
            } else if (choice.equals("0")) {
                break;
            }
        }
    }
    
    private void viewMyDeposits() throws SQLException {
        ConsoleUI.printHeader("DEPOZİTO BİLGİLERİM");
        
        List<Rental> rentals = rentalService.getUserRentals(authService.getCurrentUser().getId());
        
        if (rentals.isEmpty()) {
            ConsoleUI.printInfo("Henüz kiralama bulunmamaktadır.");
            return;
        }
        
        // Sadece depozitolu kiralamaları filtrele
        List<Rental> depositsRentals = new ArrayList<>();
        for (Rental rental : rentals) {
            if (rental.getDepositAmount().compareTo(BigDecimal.ZERO) > 0) {
                depositsRentals.add(rental);
            }
        }
        
        if (depositsRentals.isEmpty()) {
            ConsoleUI.printInfo("Depozito gerektiren kiralama bulunmamaktadır.");
            return;
        }
        
        int currentPage = 1;
        int pageSize = 5;
        int totalPages = (int) Math.ceil((double) depositsRentals.size() / pageSize);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        while (true) {
            int startIndex = (currentPage - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, depositsRentals.size());
            
            System.out.println("\n=== Sayfa " + currentPage + " / " + totalPages + " ===\n");
            
            for (int i = startIndex; i < endIndex; i++) {
                Rental rental = depositsRentals.get(i);
                System.out.println("\nKiralama ID: " + rental.getId());
                
                try {
                    Vehicle vehicle = vehicleService.getVehicleById((long) rental.getVehicleId());
                    System.out.println("Araç: " + vehicle.getBrand() + " " + vehicle.getModel());
                } catch (VehicleNotFoundException e) {
                    System.out.println("Araç ID: " + rental.getVehicleId());
                }
                
                System.out.println("Depozito Tutarı: " + rental.getDepositAmount() + " TL");
                System.out.println("Kiralama Durumu: " + rental.getStatus().getDisplayName());
                
                try {
                    DepositDAO depositDAO = new DepositDAO();
                    var depositOpt = depositDAO.findByRentalId(rental.getId());
                    if (depositOpt.isPresent()) {
                        Deposit deposit = depositOpt.get();
                        System.out.println("Depozito Durumu: " + deposit.getDepositStatus().getDisplayName());
                        if (deposit.getRefundDate() != null) {
                            System.out.println("İade Tarihi: " + deposit.getRefundDate().format(formatter));
                            System.out.println("İade Tutarı: " + deposit.getRefundAmount() + " TL");
                        }
                        if (deposit.getNotes() != null && !deposit.getNotes().isEmpty()) {
                            System.out.println("Not: " + deposit.getNotes());
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Depozito detayları alınamadı.");
                }
                
                ConsoleUI.printSeparator();
            }
            
            System.out.println("\nNavigasyon:");
            if (currentPage < totalPages) {
                System.out.println("N - Sonraki Sayfa");
            }
            if (currentPage > 1) {
                System.out.println("P - Önceki Sayfa");
            }
            System.out.println("0 - Geri Dön");
            
            String choice = ConsoleUI.readString("Seçiminiz");
            
            if (choice.equalsIgnoreCase("N") && currentPage < totalPages) {
                currentPage++;
            } else if (choice.equalsIgnoreCase("P") && currentPage > 1) {
                currentPage--;
            } else if (choice.equals("0")) {
                break;
            }
        }
    }
    
    private void viewAllDeposits() throws SQLException {
        ConsoleUI.printHeader("TÜM DEPOZİTO KAYITLARI");
        
        DepositDAO depositDAO = new DepositDAO();
        List<Deposit> allDeposits = depositDAO.findAll();
        
        if (allDeposits.isEmpty()) {
            ConsoleUI.printInfo("Henüz depozito kaydı bulunmamaktadır.");
            return;
        }
        
        int currentPage = 1;
        int pageSize = 5;
        int totalPages = (int) Math.ceil((double) allDeposits.size() / pageSize);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        while (true) {
            int startIndex = (currentPage - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, allDeposits.size());
            
            System.out.println("\n=== Sayfa " + currentPage + " / " + totalPages + " ===\n");
            
            for (int i = startIndex; i < endIndex; i++) {
                Deposit deposit = allDeposits.get(i);
                System.out.println("\nDepozito ID: " + deposit.getId());
                System.out.println("Kiralama ID: " + deposit.getRentalId());
                
                try {
                    RentalDAO rentalDAO = new RentalDAO();
                    var rentalOpt = rentalDAO.findById((long) deposit.getRentalId());
                    if (rentalOpt.isPresent()) {
                        Rental rental = rentalOpt.get();
                        System.out.println("Kullanıcı ID: " + rental.getUserId());
                        
                        try {
                            Vehicle vehicle = vehicleService.getVehicleById((long) rental.getVehicleId());
                            System.out.println("Araç: " + vehicle.getBrand() + " " + vehicle.getModel());
                        } catch (VehicleNotFoundException e) {
                            System.out.println("Araç ID: " + rental.getVehicleId());
                        }
                    }
                } catch (SQLException e) {
                    // Devam et
                }
                
                System.out.println("Depozito Tutarı: " + deposit.getDepositAmount() + " TL");
                System.out.println("Depozito Durumu: " + deposit.getDepositStatus().getDisplayName());
                System.out.println("Depozito Tarihi: " + deposit.getDepositDate().format(formatter));
                
                if (deposit.getRefundDate() != null) {
                    System.out.println("İade Tarihi: " + deposit.getRefundDate().format(formatter));
                    System.out.println("İade Tutarı: " + deposit.getRefundAmount() + " TL");
                }
                
                if (deposit.getNotes() != null && !deposit.getNotes().isEmpty()) {
                    System.out.println("Not: " + deposit.getNotes());
                }
                
                ConsoleUI.printSeparator();
            }
            
            System.out.println("\nNavigasyon:");
            if (currentPage < totalPages) {
                System.out.println("N - Sonraki Sayfa");
            }
            if (currentPage > 1) {
                System.out.println("P - Önceki Sayfa");
            }
            System.out.println("0 - Geri Dön");
            
            String choice = ConsoleUI.readString("Seçiminiz");
            
            if (choice.equalsIgnoreCase("N") && currentPage < totalPages) {
                currentPage++;
            } else if (choice.equalsIgnoreCase("P") && currentPage > 1) {
                currentPage--;
            } else if (choice.equals("0")) {
                break;
            }
        }
    }
    
    private void updateDepositStatus() throws SQLException {
        ConsoleUI.printHeader("DEPOZİTO DURUMU GÜNCELLE");
        
        long depositId = ConsoleUI.readLong("Depozito ID");
        
        DepositDAO depositDAO = new DepositDAO();
        var depositOpt = depositDAO.findById(depositId);
        
        if (depositOpt.isEmpty()) {
            ConsoleUI.printError("Depozito bulunamadı!");
            return;
        }
        
        Deposit deposit = depositOpt.get();
        
        System.out.println("\nMevcut Durum: " + deposit.getDepositStatus().getDisplayName());
        System.out.println("Depozito Tutarı: " + deposit.getDepositAmount() + " TL");
        
        System.out.println("\nYeni Durum:");
        System.out.println("1. Beklemede (PENDING)");
        System.out.println("2. Ödendi (PAID)");
        System.out.println("3. İade Edildi (REFUNDED)");
        
        int choice = ConsoleUI.readInt("Seçiminiz");
        
        PaymentStatus newStatus;
        switch (choice) {
            case 1 -> newStatus = PaymentStatus.PENDING;
            case 2 -> newStatus = PaymentStatus.PAID;
            case 3 -> newStatus = PaymentStatus.REFUNDED;
            default -> {
                ConsoleUI.printError("Geçersiz seçim!");
                return;
            }
        }
        
        if (newStatus == PaymentStatus.REFUNDED) {
            double refundAmount = ConsoleUI.readDouble("İade Tutarı (TL)");
            String notes = ConsoleUI.readString("Not (opsiyonel)");
            deposit.processRefund(BigDecimal.valueOf(refundAmount), notes);
        } else {
            deposit.setDepositStatus(newStatus);
        }
        
        depositDAO.update(deposit);
        ConsoleUI.printSuccess("Depozito durumu başarıyla güncellendi!");
    }
} 