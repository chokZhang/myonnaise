package com.mengyuan1998.finger_dancing.dagger;

import com.mengyuan1998.finger_dancing.ui.MainActivity;
import com.mengyuan1998.finger_dancing.ui.MainActivity_MembersInjector;
import com.mengyuan1998.finger_dancing.ui.ScanActivity;
import javax.inject.Provider;
import androidx.fragment.app.Fragment;
import dagger.MembersInjector;
import dagger.android.DispatchingAndroidInjector;

/**
 * Created by boyzhang
 * On 2018/10/30
 */
public class ScanActivity_MembersInjector implements MembersInjector<ScanActivity>{

    private final Provider<DispatchingAndroidInjector<Fragment>>
            fragmentDispatchingAndroidInjectorProvider;

    public ScanActivity_MembersInjector(
            Provider<DispatchingAndroidInjector<Fragment>> fragmentDispatchingAndroidInjectorProvider) {
        this.fragmentDispatchingAndroidInjectorProvider = fragmentDispatchingAndroidInjectorProvider;
    }

    public static MembersInjector<MainActivity> create(
            Provider<DispatchingAndroidInjector<Fragment>> fragmentDispatchingAndroidInjectorProvider) {
        return new MainActivity_MembersInjector(fragmentDispatchingAndroidInjectorProvider);
    }

    @Override
    public void injectMembers(ScanActivity instance) {
        injectFragmentDispatchingAndroidInjector(
                instance, fragmentDispatchingAndroidInjectorProvider.get());
    }

    public static void injectFragmentDispatchingAndroidInjector(
            ScanActivity instance,
            DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector) {
        instance.fragmentDispatchingAndroidInjector = fragmentDispatchingAndroidInjector;
    }

}
