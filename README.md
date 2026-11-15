# DonutShop

GUI shop kiểu "donut" (chest menu mua/bán theo danh mục) cho server **Paper/Spigot 1.21.x**,
theo đúng kiểu shop hay thấy ở các server Crystal SMP lớn (DonutSMP, CrystalPvP...): menu chính
chia theo danh mục (Khối, Quặng, Chiến đấu, Đồ ăn, Nông trại, Dược phẩm, Linh tinh), mỗi item
hiện rõ giá mua/bán, click trái để mua, click phải để bán, shift-click để mua/bán 1 chồng (64).

## Yêu cầu

- Server chạy **Paper** (hoặc fork như Purpur) bản **1.21.x** — khớp với bản Fabric 1.21 bạn
  đang dùng cho Dennis List.
- Plugin **Vault**.
- Một plugin economy bất kỳ hook vào Vault (EssentialsX, CMI, TNE...).

## Build

Sandbox mình chạy bị chặn mạng ra ngoài các repo Maven (papermc.io, jitpack...) nên **không tự
build ra file .jar được** — mình chỉ gửi được source code. Có 2 cách để bạn lấy file `.jar`:

### Cách 1 — Không cần cài gì (GitHub Actions)

1. Tạo một repo GitHub (public hay private đều được), push nguyên thư mục `donutshop/` này lên
   (đã có sẵn file `.github/workflows/build.yml`).
2. Vào tab **Actions** của repo, workflow "Build DonutShop" sẽ tự chạy sau khi push.
3. Đợi build xanh (~1 phút), bấm vào lần chạy đó, kéo xuống mục **Artifacts**, tải file
   `DonutShop-jar.zip` về — giải nén ra là có `DonutShop.jar`.

### Cách 2 — Build trên máy bạn (cần Java 17 + Maven, có mạng)

```bash
cd donutshop
mvn clean package
```

File jar ra lò nằm ở `target/DonutShop.jar`.

Dù build cách nào, cứ copy `DonutShop.jar` vào thư mục `plugins/` của server, khởi động lại,
xong.

## Lệnh & quyền

| Lệnh | Mô tả | Quyền |
|---|---|---|
| `/shop` (alias `/cshop`, `/donutshop`) | Mở menu chính | `donutshop.use` (mặc định: ai cũng có) |
| `/sell hand` | Bán item đang cầm trên tay | `donutshop.use` |
| `/sell all` | Bán toàn bộ item bán được trong túi đồ | `donutshop.use` |
| `/shopadmin reload` | Reload lại `config.yml` không cần restart server | `donutshop.admin` (mặc định: OP) |

## Tùy chỉnh config.yml

Mỗi danh mục trong `categories:` có:

```yaml
blocks:
  display-name: "&f&lKhoi & Xay Dung"
  icon: GRASS_BLOCK      # material làm icon trong menu chinh
  slot: 10               # vi tri trong menu chinh (0 den rows*9 - 1)
  items:
    - material: STONE
      buy: 2.0           # gia mua 1 item, dat -1 neu khong ban mua
      sell: 1.0          # gia ban 1 item, dat -1 neu khong nhan ban
```

- Tên material phải đúng theo enum `Material` của Bukkit/Paper (viết HOA, gạch dưới — ví dụ
  `DIAMOND_SWORD`, `NETHERITE_INGOT`).
- Muốn thêm danh mục mới: thêm một key mới dưới `categories:` theo đúng cấu trúc trên, chọn
  `slot` còn trống trong `main-menu-rows` hàng của menu chính.
- Đổi `main-menu-rows` (1-6) nếu muốn menu chính to/nhỏ hơn 27-54 ô.
- Mã màu dùng ký tự `&` (ví dụ `&c` đỏ, `&a` xanh lá, `&l` in đậm).

## Giới hạn hiện tại (những gì mình chưa làm để giữ code gọn)

- Nhận diện item để bán chỉ dựa theo **Material**, chưa phân biệt item có enchant/tên riêng —
  nếu không muốn người chơi bán nhầm rìu/kiếm đã enchant, nên bỏ các item trang bị ra khỏi
  danh mục `combat` và chỉ để nguyên liệu thô.
- Chưa có log giao dịch ra file/database — nếu cần thống kê doanh thu shop, có thể bổ sung thêm
  (ví dụ ghi vào SQLite) sau.
- Chưa có hiệu ứng giá biến động theo cung/cầu (kiểu BuycraftX) — toàn bộ giá cố định trong
  config, đúng phong cách donut-shop cổ điển.

Nếu bạn muốn mình nối thêm phần nào (log giao dịch, GUI xác nhận bán all, giá biến động, sync
với web Dennis List...) thì nói mình làm tiếp.
