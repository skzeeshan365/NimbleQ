package com.reiserx.nimbleq.Activities.Fragments.ClassView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.reiserx.nimbleq.Adapters.LearnerListAdapter;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.SharedPreferenceClass;
import com.reiserx.nimbleq.Utils.StateCityData;
import com.reiserx.nimbleq.Utils.dialogs;
import com.reiserx.nimbleq.ViewModels.AdministrationViewModel;
import com.reiserx.nimbleq.databinding.FragmentUserlistAdminBinding;

import java.util.ArrayList;
import java.util.List;

public class FragmentLearnerListForClass extends Fragment implements MenuProvider {

    private FragmentUserlistAdminBinding binding;

    LearnerListAdapter adapter;

    List<String> filters, gradelist, citylist;
    List<UserData> userData, userData1;

    AdministrationViewModel administrationViewModel;

    static int spinner_flag = 1;
    static String state;

    static int FILTER_BY_GRADE = 1;
    static int FILTER_BY_GENDER = 2;
    static int FILTER_BY_STATE = 3;
    static int FILTER_BY_CITY = 4;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserlistAdminBinding.inflate(inflater, container, false);

        binding.recycler.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);

        initializeSpinners();

        dialogs dialogs = new dialogs(requireContext(), binding.getRoot());
        userData = new ArrayList<>();
        userData1 = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recycler.setLayoutManager(layoutManager);
        adapter = new LearnerListAdapter(getContext(), dialogs);

        requireActivity().removeMenuProvider(this);
        requireActivity().addMenuProvider(this, getViewLifecycleOwner());

        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        administrationViewModel = new ViewModelProvider(this).get(AdministrationViewModel.class);

        SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(requireContext());

        administrationViewModel.getLearnerListForClass(sharedPreferenceClass.getClassID());
        administrationViewModel.getGetUserListMutableLiveData().observe(getViewLifecycleOwner(), userDataList -> {
            userData.clear();
            userData.addAll(userDataList);
            adapter.setData(userDataList);
            binding.recycler.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            binding.recycler.setVisibility(View.VISIBLE);
            binding.progHolder.setVisibility(View.GONE);
        });
        administrationViewModel.getUserListErrorMutableLiveData().observe(getViewLifecycleOwner(), s -> {
            binding.textView9.setText(s);
            binding.recycler.setVisibility(View.GONE);
            binding.progHolder.setVisibility(View.VISIBLE);
            binding.progressBar2.setVisibility(View.GONE);
            binding.textView9.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    void initializeSpinners() {
        filters = new ArrayList<>();

        filters.add("Select your filter");
        filters.add("Filter by grade");
        filters.add("Filter by gender");
        filters.add("Filter by state");
        filters.add("Filter by city");

        binding.spinner3.setVisibility(View.GONE);
        binding.spinner4.setVisibility(View.GONE);

        ArrayAdapter<String> subjectsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, filters);
        subjectsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner2.setAdapter(subjectsAdapter);
        binding.spinner2.setOnItemSelectedListener(new filterClassListener());
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menu.clear();
        menuInflater.inflate(R.menu.single_search_menu, menu);

        MenuItem searchViewItem
                = menu.findItem(R.id.app_bar_search);
        SearchView searchView
                = (SearchView) MenuItemCompat
                .getActionView(searchViewItem);

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        userData1 = filter(userData, newText);
                        adapter.setFilter(userData1);
                        return false;
                    }
                });
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    private List<UserData> filter(List<UserData> dataList, String newText) {
        newText = newText.toLowerCase();
        String name;
        userData1.clear();
        for (UserData dataFromDataList : dataList) {
            name = dataFromDataList.getUserName().toLowerCase();

            if (name.contains(newText)) {
                userData1.add(dataFromDataList);
            }
        }
            if (userData1.isEmpty()) {
                binding.textView9.setText(getString(R.string.users_not_avail));
                binding.recycler.setVisibility(View.GONE);
                binding.progHolder.setVisibility(View.VISIBLE);
                binding.progressBar2.setVisibility(View.GONE);
                binding.textView9.setVisibility(View.VISIBLE);
            } else {
                binding.recycler.setVisibility(View.VISIBLE);
                binding.progHolder.setVisibility(View.GONE);
        }
        return userData1;
    }

    private class filterClassListener implements AdapterView.OnItemSelectedListener {

        ArrayAdapter<String> subjectsAdapter;
        StateCityData stateCityData;

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            switch (i) {
                case 1:
                    administrationViewModel.getGradeList();
                    administrationViewModel.getListStringMutableLiveData().observe(getViewLifecycleOwner(), stringList -> {
                        binding.spinner3.setVisibility(View.VISIBLE);
                        gradelist = stringList;
                        subjectsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, stringList);
                        subjectsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        binding.spinner3.setAdapter(subjectsAdapter);
                        binding.spinner3.setOnItemSelectedListener(new gradeListClass());
                    });
                    spinner_flag = FILTER_BY_GRADE;
                    break;
                case 2:
                    binding.spinner3.setVisibility(View.VISIBLE);
                    gradelist = new ArrayList<>();
                    gradelist.add("Select gender");
                    gradelist.add("Male");
                    gradelist.add("Female");
                    subjectsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, gradelist);
                    subjectsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinner3.setAdapter(subjectsAdapter);
                    binding.spinner3.setOnItemSelectedListener(new gradeListClass());
                    spinner_flag = FILTER_BY_GENDER;
                    break;
                case 3:
                    binding.spinner3.setVisibility(View.VISIBLE);

                    stateCityData = new StateCityData(getContext());
                    gradelist = stateCityData.getStates(stateCityData.loadJSONFile());

                    subjectsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, gradelist);
                    subjectsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinner3.setAdapter(subjectsAdapter);
                    binding.spinner3.setOnItemSelectedListener(new gradeListClass());
                    spinner_flag = FILTER_BY_STATE;
                    break;
                case 4:
                    binding.spinner3.setVisibility(View.VISIBLE);
                    binding.spinner4.setVisibility(View.VISIBLE);

                    stateCityData = new StateCityData(getContext());

                    gradelist = stateCityData.getStates(stateCityData.loadJSONFile());

                    subjectsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, gradelist);
                    subjectsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinner3.setAdapter(subjectsAdapter);
                    binding.spinner3.setOnItemSelectedListener(new gradeListClass());
                    spinner_flag = FILTER_BY_CITY;
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class gradeListClass implements AdapterView.OnItemSelectedListener {

        ArrayAdapter<String> subjectsAdapter;

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (i > 0) {
                if (spinner_flag == FILTER_BY_CITY) {

                    StateCityData stateCityData = new StateCityData(getContext());

                    citylist = stateCityData.getCities(stateCityData.loadJSONFile(), gradelist.get(i));

                    subjectsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, citylist);
                    subjectsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinner4.setAdapter(subjectsAdapter);
                    binding.spinner4.setOnItemSelectedListener(new cityListClass());
                    state = gradelist.get(i);

                } else {
                    removeDuplicates(userData, spinner_flag, gradelist.get(i));
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class cityListClass implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (i > 0) {
                removeDuplicates(userData, state, citylist.get(i));
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    void removeDuplicates(List<UserData> data, int requestCode, String value) {
        List<UserData> dataList = new ArrayList<>();

        for (UserData userData : data) {
            switch (requestCode) {
                case 1:
                    if (userData.getUserDetails().getGrade().equals(value))
                        dataList.add(userData);
                    break;
                case 2:
                    if (userData.getUserDetails().getGender().equals(value))
                        dataList.add(userData);
                    break;
                case 3:
                    if (userData.getUserDetails().getState().equals(value))
                        dataList.add(userData);
                    break;
                default:
                    dataList.addAll(data);
                    break;
            }
        }
        if (!dataList.isEmpty()) {
            adapter.setData(dataList);
            binding.recycler.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            binding.textView9.setText(getString(R.string.no_user_avail_for_this_filter));
            binding.recycler.setVisibility(View.GONE);
            binding.progHolder.setVisibility(View.VISIBLE);
            binding.progressBar2.setVisibility(View.GONE);
            binding.textView9.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    void removeDuplicates(List<UserData> data, String state, String city) {
        List<UserData> dataList = new ArrayList<>();

        for (UserData userData : data) {
            if (userData.getUserDetails().getState().equals(state) && userData.getUserDetails().getCity().equals(city))
                dataList.add(userData);
        }
        if (!dataList.isEmpty()) {
            adapter.setData(dataList);
            binding.recycler.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            binding.textView9.setText(getString(R.string.no_user_avail_for_this_filter));
            binding.recycler.setVisibility(View.GONE);
            binding.progHolder.setVisibility(View.VISIBLE);
            binding.progressBar2.setVisibility(View.GONE);
            binding.textView9.setVisibility(View.VISIBLE);
        }
    }
}