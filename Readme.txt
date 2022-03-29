Server:
	Lớp chứa hàm main: ConnectForm.java
	Khi ấn connect server sẽ được khởi động ở port 3000 và tạo ra một thread để lắng nghe kết nối từ các client 
	Khi disconnect server sẽ ngắt kết nối
Client:
	Chức năng Sign up: Nhập các thông tin cần thiết và nhấn Sign up
			   sau khi sign up, form đăng nhập sẽ trở lại và yêu cầu đăng nhập
	Chức năng đăng nhập: Nhập username và passwword, nếu đăng nhập thành công sẽ vào chat frame, nếu tài
			     khoản đang online, sẽ hiện thông báo lỗi, tương tự với nhập sai tên và password
	Chat Frame: hiển thị thông tin các user đang online, hiển thị tên của bản thân ở gốc trái trên
		Nút download: Nhấn chọn file cần download và nhấn nút download, khi ấn JFileChooser sẽ hiện
			      lên yêu cầu bạn nhập nơi tải về
		JList Danh sách User: Chọn một user đang online để chat, Khi chỉ có 1 mình bạn online 
				      Jlist sẽ trống
		JList Chat: Hiển thị tin nhắn với một user khác, hiển thị tên file do mình hoặc người khác
			    gửi đến/gửi đi. Để tải file, click vào value là tên file trong Jlist sau đó ấn download.
			    Nếu chọn một phần tử không phải file và ấn download sẽ hiện thông báo lỗi. Người gửi
			    không thể tải file mình đã gửi, chỉ có người nhận có thể tải. Nếu không nhập tin nhắn
			    (Phần text feild tin nhắn trống) khi ấn send sẽ không được gửi đi
		CHức năng gửi file: Chọn một user đang online, sau đó ấn Choose file, JFIleCHooser sẽ hiện ra
				    chọn 1 file và ấn Send, Nếu tin nhắn trống, tin nhắn này sẽ không được gửi.
				    Tương tự với danh sách các user online
Chạy chương trình: Nếu Server chưa được khởi động thì client không để đăng nhập hay đăng kí 
	Chạy Server: Vào thư mục FileJarAndData để chạy file jar thực thi (ChatServerApp.jar) hoặc Thầy có thể chạy trực tiếp 
		     bằng cách mở thư mục Source -> ChatServerApp để chạy trực tiếp trên Netbeans
	Chạy Client: Tương tự như của Server
	