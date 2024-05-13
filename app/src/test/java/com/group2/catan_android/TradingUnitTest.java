package com.group2.catan_android;

import static org.mockito.Mockito.*;
import com.group2.catan_android.fragments.TradingTimeSelection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import android.widget.TextView;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import org.robolectric.RobolectricTestRunner;

@Nested
@RunWith(RobolectricTestRunner.class)
class TradingTimeSelectionTest {
        @Test
        public void testGetNumberofTextView() {
            // Mock TextView
            TextView mockTextView = mock(TextView.class);
            when(mockTextView.getText()).thenReturn("5s");

            // Test
            TradingTimeSelection fragment = new TradingTimeSelection();
            int result = fragment.getNumberofTextView(mockTextView);
            assertEquals(5, result);
        }
}


