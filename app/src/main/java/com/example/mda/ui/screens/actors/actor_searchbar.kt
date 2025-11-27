//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//
//@Composable
//fun ActorSearchBar(
//    searchText: String,
//    onSearchChange: (String) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    OutlinedTextField(
//        value = searchText,
//        onValueChange = onSearchChange,
//        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
//        trailingIcon = {
//            if (searchText.isNotEmpty()) {
//                IconButton(onClick = { onSearchChange("") }) {
//                    Icon(Icons.Default.Close, contentDescription = null)
//                }
//            }
//        },
//        placeholder = { Text("Search actors by name") },
//        singleLine = true,
//        shape = RoundedCornerShape(24.dp),
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp)
//            .height(56.dp)
//    )
//}
