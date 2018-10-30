package it.ncorti.emgvisualizer.dagger;

import javax.inject.Provider;

import androidx.fragment.app.Fragment;
import dagger.MembersInjector;
import dagger.android.DispatchingAndroidInjector;
import it.ncorti.emgvisualizer.ui.MainActivity;
import it.ncorti.emgvisualizer.ui.MainActivity_MembersInjector;
import it.ncorti.emgvisualizer.ui.ScanActivity;

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
