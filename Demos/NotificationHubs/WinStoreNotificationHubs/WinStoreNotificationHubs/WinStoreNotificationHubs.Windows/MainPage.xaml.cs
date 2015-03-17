using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI.Popups;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=234238

namespace WinStoreNotificationHubs
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class MainPage : Page
    {
        public MainPage()
        {
            this.InitializeComponent();
            lblInfo.Text = "Not registerd with Notification Hubs";
        }

        private async void btnRegisterWithNoTag_Click(object sender, RoutedEventArgs e)
        {
            var result = await App.HUB.RegisterNativeAsync(App.CHANNEL.Uri);

            // Displays the registration ID so you know it was successful
            if (result.RegistrationId != null)
            {
                var dialog = new MessageDialog("Registration w/o tags successful: " + result.RegistrationId);
                dialog.Commands.Add(new UICommand("OK"));
                lblInfo.Text = "Registered w/o tags";
                await dialog.ShowAsync();
            }
        }

        private async void btnRegisterWithTags_Click(object sender, RoutedEventArgs e)
        {
            var result = await App.HUB.RegisterNativeAsync(App.CHANNEL.Uri, new string[]{"AllUsers", "WindowsUser"});

            // Displays the registration ID so you know it was successful
            if (result.RegistrationId != null)
            {
                var dialog = new MessageDialog("Registration with tags successful: " + result.RegistrationId);
                dialog.Commands.Add(new UICommand("OK"));
                lblInfo.Text = "Registered w tags";
                await dialog.ShowAsync();
            }
        }

        private async void btnRegisterWithTemplate_Click(object sender, RoutedEventArgs e)
        {
            string template = "<toast><visual><binding template=\"ToastText01\"><text id=\"1\">$(message)</text></binding></visual></toast>";
            var result = await App.HUB.RegisterTemplateAsync(App.CHANNEL.Uri, template, "Message", new string[] { "AllUsers", "WindowsUser" });

            // Displays the registration ID so you know it was successful
            if (result.RegistrationId != null)
            {
                var dialog = new MessageDialog("Registration with template successful: " + result.RegistrationId);
                dialog.Commands.Add(new UICommand("OK"));
                lblInfo.Text = "Registered w templates";
                await dialog.ShowAsync();
            }
        }
    }
}
