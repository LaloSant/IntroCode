module com.javafxprueba {
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.graphics;
	requires atlantafx.base;
	requires lombok;

	opens com.introcode to javafx.fxml;
	opens com.introcode.controller to javafx.fxml;

	exports com.introcode;
}
