package de.munsel.spiderlamp.actors;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 * Created by munsel on 29.12.15.
 */
public class CustomTextField extends TextField {
    public CustomTextField(String text, Skin skin) {
        super(text, skin);
    }

    public CustomTextField(String text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public CustomTextField(String text, TextFieldStyle style) {
        super(text, style);
        setMaxLength(3);
        setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
    }

    @Override
    protected InputListener createInputListener() {
        return new CustomInputListener();
    }

    class CustomInputListener extends TextFieldClickListener{

        @Override
        public void clicked(InputEvent event, float x, float y) {
            setText("");
            super.clicked(event, x, y);
        }

        @Override
        public boolean keyTyped(InputEvent event, char character) {
            if (getText().length()==3 && character != Input.Keys.BACKSPACE) return true;
            return super.keyTyped(event, character);
        }
    }




}
