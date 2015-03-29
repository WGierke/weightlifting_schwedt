package de.schwedt.weightlifting.app.faq;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import de.schwedt.weightlifting.app.FaqFragment;
import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.WeightliftingApp;

public class FaqAnswerFragment extends Fragment {

    private WeightliftingApp app;
    private View fragment;
    private FaqItem faq;

    private TextView heading;
    private TextView question;
    private TextView answer;
    private ScrollView scrollView;

    public FaqAnswerFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(WeightliftingApp.TAG, "Showing Article fragment");

        fragment = inflater.inflate(R.layout.faq_answer, container, false);
        app = (WeightliftingApp) getActivity().getApplicationContext();

        heading = (TextView) fragment.findViewById(R.id.faq_heading);
        question = (TextView) fragment.findViewById(R.id.faq_question);
        answer = (TextView) fragment.findViewById(R.id.faq_answer);
        scrollView = (ScrollView) fragment.findViewById(R.id.article_scrollView);

        // Get article information from bundle
        try {
            Bundle bundle = this.getArguments();
            int position = bundle.getInt("item");
            faq = FaqFragment.faqEntries.get(position);
            showAnswer();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return fragment;
    }


    private void showAnswer() {
        heading.setText(faq.getHeader());
        question.setText(faq.getQuestion());
        answer.setText(faq.getAnswer());
    }
}
